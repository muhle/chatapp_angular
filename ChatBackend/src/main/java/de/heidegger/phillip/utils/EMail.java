package de.heidegger.phillip.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

@JsonSerialize(using = EMail.EmailTypeSerializer.class)
public class EMail {
    private final String email;

    public EMail(String email) {
        if (email == null) {
            throw new NullPointerException();
        }
        this.email = email;
    }

    public String asText() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EMail eMail = (EMail) o;

        return email.equals(eMail.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    @Override
    public String toString() {
        return "EMail{" + email + '}';
    }

    public static class EmailTypeSerializer extends JsonSerializer<EMail> {
        @Override
        public void serialize(EMail value, JsonGenerator jgen,
                              SerializerProvider provider)
                throws IOException, JsonProcessingException {
            jgen.writeString(value.asText());
        }

    }
}
