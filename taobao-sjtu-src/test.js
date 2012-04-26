function page_viewcount(view) {

	document.getElementById('J_EmItemViews').innerHTML = view['ICVT_7_13871713569'];

}

(function() {

	TShop
			.use(
					'mod~sku',
					function(T) {

						TShop.mods.SKU.Setup
								.init({

									"apiMonitorBuy" : "http://asyncwebserver.monitor.taobao.com/item?",
									kaixinShare : {
										rpara : '74dec9cf3e0bdf43a0a54b45627067d0_100002_13871713569_笑春风smile'
									},

									taojianghuShare : {
										itemId : '13871713569',
										type : '9',
										dbNum : ''
									},

									rstUrl : "http://detailskip.taobao.com/recommended_same_type_items.htm",

									rstShopId : 61468083,
									rstItemId : 13871713569,

									rstdk : 0,

									rstShopcatlist : ",308468587,",

									valLoginIndicator : "http://buy.taobao.com/auction/buy.htm?from=itemDetail&x_id=&id=13871713569",

									valFastBuyUrl : "http://buy.taobao.com/auction/fastbuy/fast_buy.htm",
									valItemId : "13871713569",

									valImageInfo : {},

									"valCartInfo" : {
										"hotItemsUrl" : "http://detailskip.taobao.com/json/cart_recommend_items.htm?shop_id=61468083&seller_id_num=278373237",
										"itemId" : "13871713569",
										"dbNum" : "",
										"cartUrl" : "http://cart.taobao.com/my_cart.htm"
									},
									"apiRelateMarket" : "http://tui.taobao.com/api/item?p=i&csk=shopping_cart&from=shopping_cart_detail&pid=13871713569&count=4&f=jsonp",

									"apiTaoCoin" : "http://taojinbi.taobao.com/trade/tradeDetailHook.htm?itemid=13871713569",

									"apiBidCount" : "http://detailskip.taobao.com/json/show_bid_count.htm?itemNumId=13871713569&old_quantity=200&date=1335008170000",

									"apiAddCart" : "http://cart.taobao.com/add_cart_item.htm?item_id=13871713569&bankfrom=",

									"valVipRate" : 0,

									"valPointRate" : "0",

									"apiPromoData" : "http://detailskip.taobao.com/json/promotionList.htm?tbviponly=false&chnl=pc&price=11950&skuPvPairs=&itemId=13871713569&sellerId=278373237&shopId=&ump=true",

									"apiItemViews" : "http://count.tbcdn.cn/counter3?keys=ICVT_7_13871713569&inc=ICVT_7_13871713569&callback=page_viewcount&sign=993844cb05cc08eb72321b0abbd14140a0da",

									"apiItemReviews" : "http://count.tbcdn.cn/counter3?keys=ICE_3_feedcount-13871713569",

									"apiItemCollects" : "http://count.tbcdn.cn/counter3?keys=ICCP_1_13871713569",

									"valTimeLeft" : "176536",

									"apiItemDesc" : "http://dsc.taobaocdn.com/i8/131/710/13871713569/T1H0LXXhFtXXcWeqbX.desc%7Cvar%5Edesc%3Bsign%5E366d1480ccb8dbc035023dc3672922b4%3Blang%5Egbk%3Bt%5E1335345884",
									"valItemIdStr" : "13871713569",

									"valReviewsApi" : "http://rate.taobao.com/detail_rate.htm?userNumId=278373237&auctionNumId=13871713569&showContent=1&currentPage=1&ismore=0&siteID=1",

									"reportApi" : "http://item.taobao.com/json/report_api.htm",
									"redirectUrl" : "http://item.taobao.com/report_redirect_url.htm",
									"valShowReviews" : false,

									"valPostFee" : {
										currCityDest : ''
									},

									getShippingInfo : "http://detailskip.taobao.com/json/postage_fee.htm?opt=&catid=50012938&ic=1&id=13871713569&il=%BA%DA%C1%FA%BD%AD%B9%FE%B6%FB%B1%F5&ap=false&ss=false&free=true&tg=false&tid=0&sid=278373237&iv=119.50&up=0.00&exp=0.00&ems=0.00&iw=0&is=",
									getDealQuantity : "http://detailskip.taobao.com/json/deal_quantity.htm?isStart=false&external=false&exterShop=false&sellerId=278373237&id=13871713569&shopId=&aucType=b&isarchive=&cartBut=true",

									ifq : "http://detailskip.taobao.com/json/ifq.htm?id=13871713569&sid=278373237&p=1&ic=1&il=%BA%DA%C1%FA%BD%AD%B9%FE%B6%FB%B1%F5&ap=0&ss=0&free=1&tg=0&tid=0&iv=119.50&up=0.00&exp=0.00&ems=0.00&iw=0&is=&q=1&start=0&ex=0&exs=0&shid=&at=b&arc=&ct=1",

									"valItemInfo" : {}

								});
					});
})();