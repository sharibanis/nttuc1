package com.sharib.nttuc1;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Document {

	@Id
    @GeneratedValue
    private Long id;
}
