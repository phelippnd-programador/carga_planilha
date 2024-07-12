package carga_planilha.domain;

public interface ValidacaoCargaComValidacaoExterna <S,O extends Object> extends ValidacaoCarga{
	void validacaoForaDaPlanilha(S service, O objeto, Integer index);	
}
