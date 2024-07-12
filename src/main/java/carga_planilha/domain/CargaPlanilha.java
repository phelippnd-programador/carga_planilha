package carga_planilha.domain;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import carga_planilha.domain.annotation.ValuePlanilha;

public class CargaPlanilha<E> {
	private Map<Integer, String> cabecalho;
	private Integer linhaCabecalho;
	private ValidacaoCarga validacaoCarga;
	private Sheet sheet;
	private String nomeAba;
	private Workbook workbook;

	public CargaPlanilha(Sheet sheet) {
		cargaInicial(sheet);
	}
	public CargaPlanilha(Workbook workbook) {
		this.workbook = workbook;
	}
	
	public CargaPlanilha(ValidacaoCarga validacaoCarga, Sheet sheet) {
		this(sheet);
		this.validacaoCarga = validacaoCarga;
	}
	public void alteraAba(String aba) {
		alteraAba(aba,null);
	}
	public void alteraAba(String aba,ValidacaoCarga validacaoCarga) {
		if (workbook != null) {
			cargaInicial(workbook.getSheet(aba));
			this.validacaoCarga = validacaoCarga;
		}
	}	

	public Row rowCabecalho(Sheet sheet) {
		int numMergedRegions = sheet.getNumMergedRegions();
		if (numMergedRegions != 0) {
			return sheet.getRow(1);
		}
		return sheet.getRow(0);
	}

	public Map<Integer, String> carregaCabecalho(Row cabecalho) {
		Map<Integer, String> cabecalhoMap = new HashMap<>();
		short ultimaCelula = cabecalho.getLastCellNum();
		for (int i = 0; i < ultimaCelula; i++) {
			Cell celula = cabecalho.getCell(i);
			if (celula == null) {
				continue;
			}
			cabecalhoMap.put(i, celula.getRichStringCellValue().toString());
		}
		return cabecalhoMap;
	}

	public void carregaValor(E objeto, Integer linha) {
		Row row = sheet.getRow(linha);
		if (linha <= ultimaLinha()) {
			carregaValor(objeto, row);
		}
	}

	public void carregaValor(E objeto, Row linha) {
		Field[] campos = objeto.getClass().getDeclaredFields();
		for (int i = 0; i < this.cabecalho.size(); i++) {
			String nomeCabecalhoCelula = this.cabecalho.get(i);
			for (int j = 0; j < campos.length; j++) {
				Field campo = campos[j];
				Cell celula = linha.getCell(i);
				ValuePlanilha annotation = campo.getDeclaredAnnotation(ValuePlanilha.class);
				if (annotation != null && nomeCabecalhoCelula.trim().equalsIgnoreCase(annotation.nome().trim())) {
					try {
						setValor(objeto, celula, campo, annotation.tipo(), nomeCabecalhoCelula);
					} catch (Exception e) {
						validacaoCarga.adicionarErro(linha.getRowNum() + 1, nomeAba, nomeCabecalhoCelula,
								e.getMessage());
					}
					break;
				}			
			}
		}
	}

	public void setValor(E objeto, Cell celula, Field campo, Class tipo, String nomeCabecalhoCelula) {
		Object valor = null;
		if (celula != null && celula.getCellType() != Cell.CELL_TYPE_BLANK) {
			if (tipo.equals(String.class)) {
				valor = carregaCellTypeString(celula);
			} else if ((tipo.equals(Integer.class) || tipo.equals(Double.class) )
					&& celula.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					valor = (int) celula.getNumericCellValue();			
			}  else if (tipo.equals(LocalDateTime.class)) {	
				valor = celula.getDateCellValue() == null ? null
						: celula.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			} else if (tipo.equals(Date.class)) {
				Date data = celula.getDateCellValue();
				valor = data;
			}
		}

		executaValidacao(nomeCabecalhoCelula, valor);
		try {
			campo.setAccessible(true);
			campo.set(objeto, valor);
			campo.setAccessible(false);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	private Object carregaCellTypeString(Cell celula) {
		if (celula.getCellType() == Cell.CELL_TYPE_STRING) {
			return  celula.getStringCellValue();
		} else if (celula.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return  String.format("%d", (int) celula.getNumericCellValue());
		} else {
			return null;
		}
	}
	private void executaValidacao(String nomeCabecalhoCelula, Object valor) {
		if (this.validacaoCarga != null && this.validacaoCarga.validacao() != null
				&& this.validacaoCarga.validacao().containsKey(nomeCabecalhoCelula)) {
			Function<Object, Void> validacaoExterna = this.validacaoCarga.validacao()
					.get(nomeCabecalhoCelula);
			validacaoExterna.apply(valor);

		}
	}

	public Row primeiraLinhaDados() {
		return sheet.getRow(this.linhaCabecalho + 1);
	}

	public Integer indexPrimeiraLinhaDados() {
		return linhaCabecalho + 1;
	}

	public Integer ultimaLinha() {
		return sheet.getLastRowNum();
	}

	private void cargaInicial(Sheet sheet) {
		this.sheet = sheet;
		Row cabecalhoRow = rowCabecalho(sheet);
		this.cabecalho = carregaCabecalho(cabecalhoRow);
		this.linhaCabecalho = cabecalhoRow.getRowNum();
		nomeAba = sheet.getSheetName();
	}

}
