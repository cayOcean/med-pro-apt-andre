package com.medpro.medpro.model.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record DadosAgendamentoConsulta(
        @NotNull Long idMedico,
        @NotNull Long idPaciente,
        @NotNull @Future LocalDateTime data) {
}