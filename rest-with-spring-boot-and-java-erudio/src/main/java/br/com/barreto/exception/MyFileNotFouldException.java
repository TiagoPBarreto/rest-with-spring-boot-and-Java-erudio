package br.com.barreto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyFileNotFouldException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public MyFileNotFouldException(String ex) {
		super(ex);
	}
	public MyFileNotFouldException(String ex, Throwable cause) {
		super(ex, cause);
	}
	

}
