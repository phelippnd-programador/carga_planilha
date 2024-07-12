package carga_planilha.domain;

import java.util.function.Function;

public interface ValidaCampo {
	public Function<Object,Void> valida();
}
