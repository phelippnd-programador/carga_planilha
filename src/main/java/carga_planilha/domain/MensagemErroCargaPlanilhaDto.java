package carga_planilha.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class MensagemErroCargaPlanilhaDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Integer linha;
	private final String aba;
	private final String colula;
	private final String mensagem;
}
