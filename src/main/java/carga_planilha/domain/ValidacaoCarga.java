package carga_planilha.domain;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

public interface ValidacaoCarga {
	public final String FORMATO_MSG_ERROR="(linha : %d - Aba : %s - Campo : %s - Mensagem : %s)";
	default void adicionarValidacao(String campo,Function<Object, Void> function) {
		validacao().put(campo, function);
	}
	public default String mensagemErro(Integer index,String aba,String campo,String mensagem) {	
		return String.format(FORMATO_MSG_ERROR,index,aba,campo,mensagem );
	}
	default void adicionarErro(Integer linha, String nomeAba, String nomeCabecalhoCelula, String message) {
		erros().add(new MensagemErroCargaPlanilhaDto(linha, nomeAba, nomeCabecalhoCelula, message));
	}
	
	HashMap<String, Function<Object, Void>> validacao();
	Set<MensagemErroCargaPlanilhaDto> erros();

}
