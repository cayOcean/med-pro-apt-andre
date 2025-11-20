package com.medpro.medpro.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medpro.medpro.model.entity.Consulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Método para validar se o médico já tem agendamento neste horário e não
    // cancelado
    boolean existsByMedicoIdAndDataAndMotivoCancelamentoIsNull(Long idMedico, LocalDateTime data);

    // Método para validar se o paciente já tem agendamento neste horário e não
    // cancelado
    boolean existsByPacienteIdAndDataAndMotivoCancelamentoIsNull(Long idPaciente, LocalDateTime data);
}