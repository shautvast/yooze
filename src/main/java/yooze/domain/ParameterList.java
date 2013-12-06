package yooze.domain;

import java.util.ArrayList;
import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ParameterList {

	private List<ParameterModel> parameters = new ArrayList<ParameterModel>();

	public static ParameterList create(CtMethod method) {
		ParameterList parameterList = new ParameterList();
		parameterList.parameters = getParameters(method);
		return parameterList;
	}

	private static List<ParameterModel> getParameters(CtMethod method) {
		try {
			List<ParameterModel> parameters = new ArrayList<ParameterModel>();
			CtClass[] parameterTypes = method.getParameterTypes();
			for (CtClass parameterType : parameterTypes) {
				parameters.add(new ParameterModel(parameterType));
			}
			return parameters;

		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public String asText() {
		String parameterText = "";
		int index = 0;
		int size = parameters.size();
		for (ParameterModel parameter : parameters) {
			parameterText = addType(parameterText, parameter);
			parameterText = addComma(parameterText, index, size);
			index++;
		}
		return parameterText;
	}

	private String addComma(String parameterText, int i, int size) {
		if (i < (size - 1)) {
			parameterText += ", ";
		}
		return parameterText;
	}

	private String addType(String parameterText, ParameterModel parameter) {
		parameterText += parameter.getType().getName();
		return parameterText;
	}

}
