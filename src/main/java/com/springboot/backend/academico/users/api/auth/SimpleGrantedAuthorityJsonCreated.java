package com.springboot.backend.academico.users.api.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleGrantedAuthorityJsonCreated {
    @JsonCreator
    public SimpleGrantedAuthorityJsonCreated(@JsonProperty("authority") String role){}
}
