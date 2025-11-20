package com.medpro.medpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.medpro.medpro.model.dto.DadosAgendamentoConsulta;
import com.medpro.medpro.model.dto.DadosCancelamentoConsulta;
import com.medpro.medpro.model.dto.DadosDetalhamentoConsulta;
import com.medpro.medpro.model.entity.Consulta;
import com.medpro.medpro.repository.ConsultaRepository;
import com.medpro.medpro.repository.MedicoRepository;
import com.medpro.medpro.repository.PacienteRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("consultas")
public class ConsultaController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoConsulta> agendar(@RequestBody @Valid DadosAgendamentoConsulta dados,
            UriComponentsBuilder uriBuilder) {

        // --- Validações (Exemplo) ---
        if (!pacienteRepository.existsById(dados.idPaciente())) {
            return ResponseEntity.badRequest().body(null); // Paciente não encontrado
        }
        if (!medicoRepository.existsById(dados.idMedico())) {
            return ResponseEntity.badRequest().body(null); // Médico não encontrado
        }

        if (consultaRepository.existsByMedicoIdAndDataAndMotivoCancelamentoIsNull(dados.idMedico(), dados.data())) {
            throw new IllegalArgumentException("Médico já possui outra consulta agendada neste horário.");
        }
        if (consultaRepository.existsByPacienteIdAndDataAndMotivoCancelamentoIsNull(dados.idPaciente(), dados.data())) {
            throw new IllegalArgumentException("Paciente já possui outra consulta agendada neste horário.");
        }
        // --- Fim das Validações ---

        var medico = medicoRepository.getReferenceById(dados.idMedico());
        var paciente = pacienteRepository.getReferenceById(dados.idPaciente());
        var consulta = new Consulta(medico, paciente, dados.data());

        consultaRepository.save(consulta);

        var uri = uriBuilder.path("/consultas/{id}").buildAndExpand(consulta.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoConsulta(consulta));
    }

    @PutMapping("/cancelar")
    @Transactional
    public ResponseEntity<DadosDetalhamentoConsulta> cancelar(@RequestBody @Valid DadosCancelamentoConsulta dados) {

        var consulta = consultaRepository.getReferenceById(dados.idConsulta());

        if (consulta.getMotivoCancelamento() != null) {
            throw new IllegalArgumentException("Esta consulta já está cancelada.");
        }

        // Lógica de validação adicional, como:
        // if (consulta.getData().isBefore(LocalDateTime.now().plusHours(24))) {
        // throw new IllegalArgumentException("Consulta só pode ser cancelada com 24h de
        // antecedência.");
        // }

        consulta.cancelar(dados.motivo());

        return ResponseEntity.ok(new DadosDetalhamentoConsulta(consulta));
    }
}