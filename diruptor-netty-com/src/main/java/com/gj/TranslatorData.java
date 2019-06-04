package com.gj;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Gjing
 **/
@Getter
@Setter
@Data
public class TranslatorData implements Serializable {
    private String id;

    private String name;

    private String message;
}
