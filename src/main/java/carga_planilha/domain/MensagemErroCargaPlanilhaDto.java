package carga_planilha.domain;

import java.io.Serializable;

public class MensagemErroCargaPlanilhaDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Integer linha;
	private final String aba;
	private final String colula;
	private final String mensagem;
	
	public MensagemErroCargaPlanilhaDto(Integer linha, String aba, String colula, String mensagem) {
		super();
		this.linha = linha;
		this.aba = aba;
		this.colula = colula;
		this.mensagem = mensagem;
	}
	public Integer getLinha() {
		return linha;
	}
	public String getAba() {
		return aba;
	}
	public String getColula() {
		return colula;
	}
	public String getMensagem() {
		return mensagem;
	}
	
}
