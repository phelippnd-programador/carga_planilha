package carga_planilha.domain;

public interface ValidacaoCargaComValidacaoExterna <O extends Object> extends ValidacaoCarga{
	void validacaoForaDaPlanilha(O objeto, Integer index);	
}
