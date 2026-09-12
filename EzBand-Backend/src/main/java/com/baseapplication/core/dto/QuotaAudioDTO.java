package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Quanto a banda ja gastou de storage de audio e quanto ainda cabe. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuotaAudioDTO {

    private long usadoBytes;
    private long limiteBytes;
    private int totalAudios;
}
