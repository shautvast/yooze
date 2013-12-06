package yooze.dto;

import java.util.ArrayList;
import java.util.List;

import yooze.domain.MethodModel;

public class MethodDto {
	private String name;
	private List<MethodDto> callers = new ArrayList<MethodDto>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<MethodDto> getCallers() {
		return callers;
	}

	public void setCallers(List<MethodDto> callers) {
		this.callers = callers;
	}

	public void addCaller(MethodDto caller) {
		callers.add(caller);
	}

	public static MethodDto create(MethodModel startMethod) {
		MethodDto methodDto = new MethodDto();
		methodDto.setName(startMethod.getFullname());
		for (MethodModel caller : startMethod.getCallers()) {
			methodDto.addCaller(create(caller));
		}
		return methodDto;
	}

}
