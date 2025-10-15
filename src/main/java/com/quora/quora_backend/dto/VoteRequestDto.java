package com.quora.quora_backend.dto;

import com.quora.quora_backend.model.VoteType;

import lombok.Data;

@Data
public class VoteRequestDto {
private VoteType voteType;
}

