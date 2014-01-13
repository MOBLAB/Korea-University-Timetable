package com.proinlab.networkmanager;

import java.util.ArrayList;

import com.proinlab.kut.functions.DataBaseHelper;

public class ParsingTool {

	public ArrayList<String> GET_LOCATION(String[] insert) {
		ArrayList<String> inTime = new ArrayList<String>();
		String insertTime = insert[DataBaseHelper.DB_INDEX_3ROW_11_SCHEDULE];
		String tmp = null;
		if (insertTime.indexOf(":") == -1) {
			tmp = insertTime.substring(insertTime.indexOf(")") + 1);
			inTime.add(tmp);
		} else {
			tmp = insertTime.substring(0, insertTime.indexOf(":"));
			tmp = tmp.substring(tmp.indexOf(")") + 1, insertTime.indexOf(":"));
			inTime.add(tmp);
			while (insertTime.indexOf(":") != -1) {
				insertTime = insertTime.substring(insertTime.indexOf(":") + 1);
				int end = insertTime.indexOf(":");
				if (end != -1)
					tmp = insertTime.substring(insertTime.indexOf(")") + 1,
							insertTime.indexOf(":"));
				else
					tmp = insertTime.substring(insertTime.indexOf(")") + 1);
				inTime.add(tmp);
			}
		}

		return inTime;
	}

	/**
	 * KUT ���� : ���������� �ð��� ȭ(3-4) �� ���� ������ ���´�
	 * 
	 * @param insert
	 *            ��ü ������
	 * @return String �迭
	 */
	public ArrayList<String> GET_PART_TIME(String[] insert) {
		ArrayList<String> inTime = new ArrayList<String>();
		String insertTime = insert[DataBaseHelper.DB_INDEX_3ROW_11_SCHEDULE];
		String tmp = null;
		if (insertTime.indexOf(":") == -1) {
			tmp = insertTime.substring(0, insertTime.indexOf(")") + 1);
			inTime.add(tmp);
		} else {
			tmp = insertTime.substring(0, insertTime.indexOf(":"));
			tmp = tmp.substring(0, tmp.indexOf(")") + 1);
			inTime.add(tmp);
			while (insertTime.indexOf(":") != -1) {
				insertTime = insertTime.substring(insertTime.indexOf(":") + 1);
				int end = insertTime.indexOf(":");
				if (end != -1)
					tmp = insertTime.substring(0, end);
				else
					tmp = insertTime.substring(0);
				tmp = tmp.substring(0, tmp.indexOf(")") + 1);
				inTime.add(tmp);
			}
		}

		return inTime;
	}

	/**
	 * KUT ���� : ���������� �ð��� ȭ1 ȭ2 ȭ3 �� ���� ������ ���´�
	 * 
	 * @param insert
	 *            ��ü ������
	 * @return String �迭
	 */

	public ArrayList<String> GET_PART_DETAIL_TIME(String[] insert) {
		ArrayList<String> inTime = new ArrayList<String>();
		String insertTime = insert[DataBaseHelper.DB_INDEX_3ROW_11_SCHEDULE];
		String tmp = null;
		if (insertTime.indexOf(":") == -1) {
			tmp = insertTime.substring(0, insertTime.indexOf(")") + 1);
			if (tmp.indexOf("-") == -1) {
				tmp = tmp.substring(0, 1) + tmp.substring(2, tmp.indexOf(")"));
				inTime.add(tmp);
			} else {
				int startT = Integer
						.parseInt(tmp.substring(2, tmp.indexOf("-")));
				int endT = Integer.parseInt(tmp.substring(tmp.indexOf("-") + 1,
						tmp.indexOf(")")));
				for (int i = startT; i <= endT; i++) {
					tmp = tmp.substring(0, 1) + Integer.toString(i);
					inTime.add(tmp);
				}
			}
		} else {
			tmp = insertTime.substring(0, insertTime.indexOf(":"));
			tmp = tmp.substring(0, tmp.indexOf(")") + 1);
			if (tmp.indexOf("-") == -1) {
				tmp = tmp.substring(0, 1) + tmp.substring(2, tmp.indexOf(")"));
				inTime.add(tmp);
			} else {
				int startT = Integer
						.parseInt(tmp.substring(2, tmp.indexOf("-")));
				int endT = Integer.parseInt(tmp.substring(tmp.indexOf("-") + 1,
						tmp.indexOf(")")));
				for (int i = startT; i <= endT; i++) {
					tmp = tmp.substring(0, 1) + Integer.toString(i);
					inTime.add(tmp);
				}
			}
			while (insertTime.indexOf(":") != -1) {
				insertTime = insertTime.substring(insertTime.indexOf(":") + 1);
				int end = insertTime.indexOf(":");
				if (end != -1)
					tmp = insertTime.substring(0, end);
				else
					tmp = insertTime.substring(0);
				tmp = tmp.substring(0, tmp.indexOf(")") + 1);
				if (tmp.indexOf("-") == -1) {
					tmp = tmp.substring(0, 1)
							+ tmp.substring(2, tmp.indexOf(")"));
					inTime.add(tmp);
				} else {
					int startT = Integer.parseInt(tmp.substring(2,
							tmp.indexOf("-")));
					int endT = Integer.parseInt(tmp.substring(
							tmp.indexOf("-") + 1, tmp.indexOf(")")));
					for (int i = startT; i <= endT; i++) {
						tmp = tmp.substring(0, 1) + Integer.toString(i);
						inTime.add(tmp);
					}
				}

			}
		}

		return inTime;
	}

	/**
	 * ���ʿ��� �����͸� �����Ѵ�.
	 * 
	 * @param data
	 * @return
	 */

	public String REMOVE_UNNECESSORY(String data) {
		data = data.replaceAll(" ", "");
		data = data.replaceAll("\\p{Space}", "");
		data = data.replaceAll("\\p{Blank}", "");
		data = data.replaceAll(System.getProperty("line.separator"), "");
		data = data.replaceAll("<br>", ":");
		data = data.replaceAll("</ br>", ":");
		data = data.replaceAll("</br>", ":");
		data = data.replaceAll("<br/>", ":");
		data = data.replaceAll("<br />", ":");
		data = data.replaceAll("<BR>", ":");
		data = data.replaceAll("</ BR>", ":");
		data = data.replaceAll("</BR>", ":");
		data = data.replaceAll("<BR/>", ":");
		data = data.replaceAll("<BR />", ":");
		data = data.replaceAll("&nbsp;", "");
		return data;
	}

	public String get_start_location(String HtmlString, String start_tag) {
		int start = HtmlString.indexOf(start_tag);
		if (start == -1)
			return HtmlString;

		HtmlString = HtmlString.substring(start);
		return HtmlString;
	}

	/**
	 * start_tag �� end_tag ������ �ҽ��� ���Ѵ�
	 * 
	 * @param HtmlString
	 * @param start_tag
	 * @param end_tag
	 * @return String
	 */
	public String PARSE_SOURCE_BY_TAG(String HtmlString, String start_tag,
			String end_tag) {
		if (HtmlString == null)
			return null;
		String parsed_data = HtmlString;
		int start = HtmlString.indexOf(start_tag);

		if (start == -1)
			return null;

		parsed_data = get_start_location(parsed_data, start_tag);

		start = parsed_data.indexOf(start_tag);

		int end = start_tag.length()
				+ parsed_data.substring(start_tag.length()).indexOf(end_tag);

		parsed_data = parsed_data.substring(start + start_tag.length(), end);

		return parsed_data;
	}

	/**
	 * Tag ���̿� �ִ� String�� ����Ʈ�� ��´�
	 * 
	 * @param HtmlString
	 * @param preTag
	 * @param endTag
	 * @return ArrayList<String>
	 */
	public ArrayList<String> PARSE_SOURCELIST_BY_TAG(String HtmlString,
			String preTag, String endTag) {
		if (HtmlString == null)
			return null;

		ArrayList<String> returnData = new ArrayList<String>();
		String parsed_data = HtmlString;

		while (parsed_data.indexOf(preTag) != -1) {
			parsed_data = get_start_location(parsed_data, preTag);
			returnData.add(PARSE_SOURCE_BY_TAG(parsed_data, preTag, endTag));
			parsed_data = parsed_data.substring(1);
		}
		return returnData;
	}

	/**
	 * <node->value</-node> ���Ŀ��� value�� ���Ѵ�
	 * 
	 * @param HtmlString
	 * @param node
	 * @return
	 */
	public String PARSE_VALUE_BY_TAG(String HtmlString, String node) {
		if (HtmlString == null)
			return null;
		String parsed_data = HtmlString;
		int start = HtmlString.indexOf(node);

		if (start == -1)
			return null;

		parsed_data = get_start_location(parsed_data, node);
		parsed_data = get_start_location(parsed_data, ">");

		parsed_data = parsed_data.substring(1, parsed_data.indexOf("<"));

		return parsed_data;
	}

	public ArrayList<String> PARSE_VALUELIST_BY_TAG(String HtmlString,
			String node) {
		if (HtmlString == null)
			return null;

		ArrayList<String> returnData = new ArrayList<String>();
		String parsed_data = HtmlString;

		while (parsed_data.indexOf(node) != -1) {
			parsed_data = get_start_location(parsed_data, node);
			returnData.add(PARSE_VALUE_BY_TAG(parsed_data, node));
			parsed_data = parsed_data.substring(1);
		}
		return returnData;
	}
}
