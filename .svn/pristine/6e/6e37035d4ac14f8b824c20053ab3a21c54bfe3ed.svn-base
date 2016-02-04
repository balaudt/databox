package com.sagarius.goddess.server.resources.mapreduce;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.sagarius.goddess.server.Utils;

public class SubjectGroup implements JSONAware {
	protected String name;
	protected int level;
	protected SubjectType type;
	protected List<SubjectGroup> children;
	protected int startColumn;
	protected int endColumn;
	protected String startColString;
	protected String endColString;
	protected String absentCondition;
	protected String missingMarksCondition;
	protected String passCondition;
	protected String totalString;
	// For class level consolidation in case of A type groups
	protected String averageString;
	// protected String maxTotalString;
	private SubjectGroup parent;

	public void setup(int minRow) {
		StrBuilder missingMarkBuilder = null;
		StrBuilder passBuilder = null;
		StrBuilder totalBuilder = null;
		// StrBuilder maxTotalBuilder = null;
		// int maxRow = minRow + 1;
		if (type != SubjectType.B) {
			absentCondition = new StrBuilder().append("COUNTIF(")
					.append(startColString).append("i:").append(endColString)
					.append("i,'=A')>0").toString();
		} else {
			absentCondition = new StrBuilder().append("COUNTIF(")
					.append(startColString).append("i:")
					.append(Utils.getColString(endColumn - 1))
					.append("i,'=A')>0").toString();
		}
		if (type == SubjectType.ZERO) {
			int noOfSubj = endColumn - startColumn + 1;
			String range = new StrBuilder(startColString).append("i:")
					.append(endColString).append("i").toString();
			switch (parent.getType()) {
			case A:
				missingMarksCondition = new StrBuilder("COUNTBLANK(")
						.append(startColString).append("i:")
						.append(endColString).append("i)=").append(noOfSubj)
						.toString();
				passCondition = new StrBuilder("COUNT(FILTER(").append(range)
						.append(",").append(range).append(">")
						.append(range.replaceAll("i", "" + minRow))
						.append("))>0").toString();
				totalString = new StrBuilder("MAX(").append(startColString)
						.append("i:").append(endColString).append("i)")
						.toString();
				// maxTotalString = new
				// StrBuilder("MAX(").append(startColString)
				// .append(maxRow).append(":").append(endColString)
				// .append(maxRow).append(")").toString();
				break;
			case B:
				missingMarksCondition = new StrBuilder("COUNTBLANK(")
						.append(startColString).append("i:")
						.append(endColString).append("i)>0").toString();
				passCondition = new StrBuilder("COUNT(FILTER(").append(range)
						.append(",").append(range).append(">")
						.append(range.replaceAll("i", "" + minRow))
						.append("))=").append(noOfSubj).toString();
				totalString = new StrBuilder("SUM(").append(startColString)
						.append("i:").append(endColString).append("i)")
						.toString();
				// maxTotalString = new
				// StrBuilder("SUM(").append(startColString)
				// .append(maxRow).append(":").append(endColString)
				// .append(maxRow).append(")").toString();
			}
			return;
		}
		for (SubjectGroup child : children) {
			child.setup(minRow);
		}
		if (children.size() > 1) {
			if (type == SubjectType.A) {
				missingMarkBuilder = new StrBuilder("AND(");
				passBuilder = new StrBuilder("OR(");
				totalBuilder = new StrBuilder("MAX(");
				// maxTotalBuilder = new StrBuilder("MAX(");
			} else {
				missingMarkBuilder = new StrBuilder("OR(");
				passBuilder = new StrBuilder("AND(");
				totalBuilder = new StrBuilder("SUM(");
				// maxTotalBuilder = new StrBuilder("SUM(");
			}

			for (SubjectGroup child : children) {
				missingMarkBuilder.append(child.missingMarksCondition).append(
						",");
				passBuilder.append(child.passCondition).append(",");
				if (child.type == SubjectType.B) {
					totalBuilder.append(child.endColString).append("i,");
					// maxTotalBuilder.append(child.endColString).append(maxRow)
					// .append(",");
				} else {
					totalBuilder.append(child.totalString).append(",");
					// maxTotalBuilder.append(child.maxTotalString).append(",");
				}
			}
			missingMarkBuilder.deleteCharAt(missingMarkBuilder.length() - 1);
			missingMarkBuilder.append(")");
			missingMarksCondition = missingMarkBuilder.toString();
			passBuilder.deleteCharAt(passBuilder.length() - 1);
			passBuilder.append(")");
			passCondition = passBuilder.toString();
			totalBuilder.deleteCharAt(totalBuilder.length() - 1);
			totalBuilder.append(")");
			totalString = totalBuilder.toString();
			// maxTotalBuilder.deleteCharAt(totalBuilder.length() - 1);
			// maxTotalBuilder.append(")");
			// maxTotalString = maxTotalBuilder.toString();
		} else {
			SubjectGroup child = children.get(0);
			missingMarksCondition = child.missingMarksCondition;
			passCondition = child.passCondition;
			if (child.type == SubjectType.B) {
				totalString = child.endColString.concat("i");
				// maxTotalString = child.endColString.concat(maxRow + "");
			} else {
				totalString = child.totalString;
				// maxTotalString = child.maxTotalString;
			}
		}
	}

	// public String getMaxTotalString() {
	// return maxTotalString;
	// }

	public String getAbsentCondition() {
		return absentCondition;
	}

	public String getMissingMarksCondition() {
		return missingMarksCondition;
	}

	public String getPassCondition() {
		return passCondition;
	}

	public String getTotalString() {
		return totalString;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public SubjectType getType() {
		return type;
	}

	public void setType(SubjectType type) {
		this.type = type;
	}

	public List<SubjectGroup> getChildren() {
		if (children == null) {
			children = new LinkedList<SubjectGroup>();
		}
		return children;
	}

	public void setChildren(List<SubjectGroup> children) {
		this.children = children;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
		startColString = Utils.getColString(startColumn);
	}

	public int getEndColumn() {
		return endColumn;
	}

	public void setEndColumn(int endColumn) {
		this.endColumn = endColumn;
		endColString = Utils.getColString(endColumn);
	}

	public String getStartColString() {
		return startColString;
	}

	public String getEndColString() {
		return endColString;
	}

	@Override
	public String toString() {
		return "SubjectGroup [name=" + name + ", level=" + level + ", type="
				+ type + ", children=" + children + ", startColumn="
				+ startColumn + ", endColumn=" + endColumn
				+ ", startColString=" + startColString + ", endColString="
				+ endColString + ", absentCondition=" + absentCondition
				+ ", missingMarksCondition=" + missingMarksCondition
				+ ", passCondition=" + passCondition + ", totalString="
				+ totalString + ", parent=" + parent + "]";
	}

	public SubjectGroup() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject result = new JSONObject();
		result.put("name", name);
		result.put("level", level);
		result.put("type", type == null ? null : type.toString());
		result.put("children", children);
		result.put("startColumn", startColString);
		result.put("endColumn", endColString);
		result.put("startIndex", startColumn);
		result.put("endIndex", endColumn);
		return result.toJSONString();
	}

	public void setParent(SubjectGroup parent) {
		this.parent = parent;
	}

	public SubjectGroup getParent() {
		return parent;
	}

}