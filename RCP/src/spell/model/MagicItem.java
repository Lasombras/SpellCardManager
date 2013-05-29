package spell.model;

import spell.Activator;
import spell.model.simple.SimpleModel;



public class MagicItem extends SimpleModel {

	private String detail = "";
	private String background = "";
	private String originalName = "";
	private String page = "";
	private String cardText = "";
	private String aura = "";
	private String casterLevel = "";
	private int slotId = 1;
	private int itemTypeId = 1;
	private int price = 0;
	private String weight = "";
	private String constructionRequirements = "";
	private int constructionCost = 0;
	private int sourceId = 1;
	
	public MagicItem(int id) {
		super(id, "", Activator.MAGIC_ITEM_NO_ICON);
		this.setBackground(Activator.MAGIC_ITEM_NO_BACKGROUND);
	}
		
	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getPage() {
		return page==null?"":page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getDetail() {
		return getDetail(true);
	}
	
	public String getDetail(boolean formatted) {
		if(formatted) {
			return detail;
		} else {
			String transform = detail;
			transform = transform.replaceAll("<b>", "").replaceAll("</b>", "");
			transform = transform.replaceAll("<i>", "").replaceAll("</i>", "");
			transform = removeParameterTag(transform, "a");
			transform = transform.replaceAll("</a>", "");
			transform = removeBlockTag(transform, "table");
			transform = transform.replaceAll("<center>", "");
			transform = transform.replaceAll("</center>", "");
			transform = transform.replaceAll("<ul>", "").replaceAll("</ul>", "");
			transform = transform.replaceAll("</li><li>", "\n<li>");
			transform = transform.replaceAll("<li>", "•").replaceAll("</li>", "");
			
			return transform;
		}
	}
	
	private String removeParameterTag(String str, String tag) {
		tag = "<" + tag + " ";
		while(str.indexOf(tag) > -1) {
			String strStart = str.substring(0,str.indexOf(tag));
			String strEnd = str.substring(str.indexOf(tag));
			strEnd = strEnd.substring(strEnd.indexOf(">")+1);
			str = strStart + strEnd;
		}
		return str;		
	}
	private String removeBlockTag(String str, String tag) {
		String startTab = "<" + tag + ">";
		String endTag = "</" + tag + ">";
		while(str.indexOf(startTab) > -1) {
			String strStart = str.substring(0,str.indexOf(startTab));
			String strEnd = str.substring(str.indexOf(endTag) + endTag.length());
			str = strStart + strEnd;
		}
		return str;		
	}


	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getCardText() {
		return cardText;
	}

	public void setCardText(String cardText) {
		this.cardText = cardText;
	}

	public String getAura() {
		return aura;
	}

	public void setAura(String aura) {
		this.aura = aura;
	}

	public String getCasterLevel() {
		return casterLevel;
	}

	public void setCasterLevel(String casterLevel) {
		this.casterLevel = casterLevel;
	}

	public int getSlotId() {
		return slotId;
	}

	
	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}

	public int getItemTypeId() {
		return itemTypeId;
	}

	public void setItemTypeId(int itemTypeID) {
		this.itemTypeId = itemTypeID;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getConstructionRequirements() {
		return constructionRequirements;
	}

	public void setConstructionRequirements(String constructionRequirements) {
		this.constructionRequirements = constructionRequirements;
	}

	public int getConstructionCost() {
		return constructionCost;
	}

	public void setConstructionCost(int constructionCost) {
		this.constructionCost = constructionCost;
	}

	
	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public MagicItem clone() {
		MagicItem magicItem = new MagicItem(0);
		magicItem.setBackground(this.getBackground());
		magicItem.setCardText(this.getCardText());
		magicItem.setDetail(this.getDetail());
		magicItem.setImage(this.getImage());
		magicItem.setDirty(this.isDirty());
		magicItem.setExist(this.isExist());
		magicItem.setOriginalName(this.getOriginalName());
		magicItem.setPage(this.getPage());
		magicItem.setTitle(this.getTitle());	
		magicItem.setAura(this.getAura());
		magicItem.setCasterLevel(this.getCasterLevel());
		magicItem.setSlotId(this.getSlotId());
		magicItem.setItemTypeId(this.getItemTypeId());
		magicItem.setPrice(this.getPrice());
		magicItem.setWeight(this.getWeight());
		magicItem.setConstructionCost(this.getConstructionCost());
		magicItem.setConstructionRequirements(this.getConstructionRequirements());
		magicItem.setSourceId(this.getSourceId());

		return magicItem;
	}
}
