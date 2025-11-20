package com.medpro.medpro.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
@CrossOrigin(origins = "http://127.0.0.1:5500") // CORS AJUSTADO
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

        // 1. Validação de Médico e Paciente Ativos
        var medico = medicoRepository.findById(dados.idMedico()).filter(m -> m.isAtivo())
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado ou inativo."));
        var paciente = pacienteRepository.findById(dados.idPaciente()).filter(p -> p.isAtivo())
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado ou inativo."));

        // 2. Validação de Horário de Funcionamento (Exemplo: Seg-Sáb, 07h-18h)
        if (dados.data().getDayOfWeek().getValue() == 7 || dados.data().getHour() < 7 || dados.data().getHour() > 18) {
            throw new IllegalArgumentException("Consulta fora do horário de funcionamento (Seg-Sáb, 07h-18h)");
        }

        // 3. Validação de Conflito de Horário do Médico
        if (consultaRepository.existsByMedicoIdAndDataAndMotivoCancelamentoIsNull(dados.idMedico(), dados.data())) {
            throw new IllegalArgumentException("Médico já possui outra consulta agendada neste horário.");
        }
        // 4. Validação de Conflito de Horário do Paciente
        if (consultaRepository.existsByPacienteIdAndDataAndMotivoCancelamentoIsNull(dados.idPaciente(), dados.data())) {
            throw new IllegalArgumentException("Paciente já possui outra consulta agendada neste horário.");
        }

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

        // Regra: Cancelamento com 24h de antecedência
        if (consulta.getData().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new IllegalArgumentException("Consulta só pode ser cancelada com 24h de antecedência.");
        }

        consulta.cancelar(dados.motivo());

        return ResponseEntity.ok(new DadosDetalhamentoConsulta(consulta));
    }

    @GetMapping
    public ResponseEntity<Page<DadosDetalhamentoConsulta>> listar(Pageable paginacao) {
        var page = consultaRepository.findAll(paginacao).map(DadosDetalhamentoConsulta::new);
        return ResponseEntity.ok(page);
    }
}