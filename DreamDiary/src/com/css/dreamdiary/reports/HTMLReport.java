/**
 * 
 */
package com.css.dreamdiary.reports;

import java.io.BufferedWriter;
import java.io.IOException;

import com.css.dreamdiary.model.DiaryEntryBean;

/**
 * @author Chaitanya.Shende
 *
 */
public class HTMLReport extends AbstractReport {

	@Override
	protected void writeHeader() {
		try {
			BufferedWriter writer = getWriter();
			writer.write("<!DOCTYPE html>");
			writer.write("<html>");
			writer.write("<head>");
			writer.write("<meta charset='ISO-8859-1'>");
			writer.write("<title>Diary report</title>");
			writer.write("<style type='text/css'>");
			writer.write("#rounded-corner");
			writer.write("{");
			writer.write("	font-family: 'Lucida Sans Unicode', 'Lucida Grande', Sans-Serif;");
			writer.write("	font-size: 12px;");
			writer.write("	margin: 45px;");
			writer.write("	width: 480px;");
			writer.write("	text-align: left;");
			writer.write("	border-collapse: collapse;");
			writer.write("}");
			writer.write("#rounded-corner thead th.rounded-company");
			writer.write("{");
			writer.write("	background: #b9c9fe  left -1px no-repeat;");
			writer.write("}");
			writer.write("#rounded-corner thead th.rounded-q4");
			writer.write("{");
			writer.write("	background: #b9c9fe  right -1px no-repeat;");
			writer.write("}");
			writer.write("#rounded-corner th");
			writer.write("{");
			writer.write("	padding: 8px;");
			writer.write("	font-weight: normal;");
			writer.write("	font-size: 13px;");
			writer.write("	color: #039;");
			writer.write("	background: #b9c9fe;");
			writer.write("}");
			writer.write("#rounded-corner td");
			writer.write("{");
			writer.write("	padding: 8px;");
			writer.write("	background: #e8edff;");
			writer.write("	border-top: 1px solid #fff;");
			writer.write("	color: #669;");
			writer.write("}");
			writer.write("#rounded-corner tfoot td.rounded-foot-left");
			writer.write("{");
			writer.write("	background: #e8edff left bottom no-repeat;");
			writer.write("}");
			writer.write("#rounded-corner tfoot td.rounded-foot-right");
			writer.write("{");
			writer.write("	background: #e8edff right bottom no-repeat;");
			writer.write("}");
			writer.write("#rounded-corner tbody tr:hover td");
			writer.write("{");
			writer.write("	background: #d0dafd;");
			writer.write("}");
			writer.write("#rounded-corner td a");
			writer.write("{");
			writer.write("	");
			writer.write("}");
			writer.write("hr ");
			writer.write("{");
			writer.write("	align: center;");
			writer.write("	size: 1px;");
			writer.write("	width: 100%;");
			writer.write("}");
			writer.write("</style>");
			writer.write("</head>");
			writer.write("<body>");
			writer.write("<table id='rounded-corner'>");
			writer.write("<caption><h2>Entries for: " + getShortCriteriaString() + "</h2></caption>");
			writer.write("<thead class='rounded-corner'>");
			writer.write("<tr>");
			writer.write("<th width='75px'>Date</th>");
			writer.write("<th width='235px'>Title</th>");
			writer.write("<th width='75px'>Type</th>");
			writer.write("<th width='75px'>Mood</th>");
			writer.write("<th width='55px'>Hours of sleep</th>");
			writer.write("<th width='500px'>Description</th>");
			writer.write("</tr>");
			writer.write("</thead>");
			writer.write("<tbody>");
			writer.flush();
		} catch (IOException e) {
			return;
		} 
	}

	@Override
	protected void writeRecordFor(DiaryEntryBean bean) {
		try {
			BufferedWriter writer = getWriter();
			writer.write("<tr>");
			writer.write("<td><a href='#id" + bean.getId() + "'>");
			writer.write(format(bean.getCreationDateTime()));
			writer.write("</a></td><td>");
			writer.write(bean.getTitle());
			writer.write("</td><td>");
			writer.write(bean.getType());
			writer.write("</td><td>");
			writer.write(bean.getMood());
			writer.write("</td><td>");
			writer.write(format(bean.getSleepHours()));
			writer.write("</td><td>");
			writer.write(bean.getDescription());
			writer.write("</td></tr>");
			writer.flush();
		} catch (IOException e) {
			return;
		} 
	}

	@Override
	protected void writeFooter() {
		try {
			BufferedWriter writer = getWriter();
			writer.write("</tbody>");
			writer.write("</table>");
			writer.write("<hr>");
			writer.flush();
			for(DiaryEntryBean bean : getListBeans()) {
				writer.write("<a id='id" + bean.getId() + "'><b>Title: </b>" + bean.getTitle() + "</a>");
				writer.write("<br><b>Date: </b>" + format(bean.getCreationDateTime()));
				writer.write("<br><b>Type: </b>" + bean.getType());
				writer.write("<br><b>Mood: </b>" + bean.getMood());
				writer.write("<br><b>Hours slept: </b>" + format(bean.getSleepHours()));
				writer.write("<br><b>Content: </b><br>" + bean.getDescription());
				writer.write("<hr>");
				writer.flush();
			}
		} catch (IOException e) {
			return;
		} 
	}
}
