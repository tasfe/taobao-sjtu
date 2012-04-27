package edu.fudan.autologin.pojos;
//"user":{
//"vip":"",
//"rank":136,
//"nick":"771665176_44",
//"userId":410769781,
//"displayRatePic":"b_red_4.gif",
//"nickUrl":"http://wow.taobao.com/u/NDEwNzY5Nzgx/view/ta_taoshare_list.htm?redirect=fa",
//"vipLevel":2,
//"avatar":"http://img.taobaocdn.com/sns_logo/i1/T1VxqHXa4rXXb1upjX.jpg_40x40.jpg",
//"anony":false,
//"rankUrl":"http://rate.taobao.com/rate.htm?user_id=410769781&rater=1"}
//},
public class FeedRateUserAttr {
	private String vip;
	private int rank;
	public String getVip() {
		return vip;
	}
	public void setVip(String vip) {
		this.vip = vip;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDisplayRatePic() {
		return displayRatePic;
	}
	public void setDisplayRatePic(String displayRatePic) {
		this.displayRatePic = displayRatePic;
	}
	public String getNickUrll() {
		return nickUrl;
	}
	public void setNickUrll(String nickUrll) {
		this.nickUrl = nickUrll;
	}
	public int getVipLevel() {
		return vipLevel;
	}
	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getRankUrl() {
		return rankUrl;
	}
	public void setRankUrl(String rankUrl) {
		this.rankUrl = rankUrl;
	}
	private String nick;
	private String userId;
	private String displayRatePic;
	private String nickUrl;
	private int vipLevel;
	private String avatar;
	private String rankUrl;
	
	private boolean anony;
	public boolean isAnony() {
		return anony;
	}
	public void setAnony(boolean anony) {
		this.anony = anony;
	}
	
}
