package com.medpro.medpro.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medpro.medpro.model.entity.Consulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    boolean existsByMedicoIdAndDataAndMotivoCancelamentoIsNull(Long idMedico, LocalDateTime data);

    boolean existsByPacienteIdAndDataAndMotivoCancelamentoIsNull(Long idPaciente, LocalDateTime data);
}