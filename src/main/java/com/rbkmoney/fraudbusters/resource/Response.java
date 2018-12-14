package com.rbkmoney.fraudbusters.resource;

import lombok.Data;

import java.util.List;

@Data
public class Response {
    List<Count> list;
}