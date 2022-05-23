package
com.letyouknow.model

class Deal {
  int buyerID;
  bool canSavePayment;
  Object card_brand;
  Object card_last4;
  int dealID;
  String dealTimeStamp;
  List<Object> dealUcdDTOs;
  Object dealerAccessoryIDs;
  int guestId;
  String initial;
  String ip;
  bool isPackageNone;
  String lastSubmissionTimeStamp;
  String loanType;
  String paymentLast4;
  String paymentMethod;
  Object payment_intent_id;
  Object payment_method_id;
  double price;
  int productID;
  Object promotionId;
  String reservedTimeStamp;
  int searchRadius;
  int seed;
  int submissionCount;
  int timeZoneOffset;
  int userID;
  int userProfileID;
  int vehicleExteriorColorID;
  Object vehicleInteriorColorID;
  Object vehicleInventoryId;
  int vehicleMakeID;
  int vehicleModelID;
  Object vehiclePackageIDs;
  int vehicleTrimID;
  int vehicleYearID;
  String zipCode;

  Deal(
      {this.buyerID, this.canSavePayment, this.card_brand, this.card_last4, this.dealID, this.dealTimeStamp, this.dealUcdDTOs, this.dealerAccessoryIDs, this.guestId, this.initial, this.ip, this.isPackageNone, this.lastSubmissionTimeStamp, this.loanType, this.paymentLast4, this.paymentMethod, this.payment_intent_id, this.payment_method_id, this.price, this.productID, this.promotionId, this.reservedTimeStamp, this.searchRadius, this.seed, this.submissionCount, this.timeZoneOffset, this.userID, this.userProfileID, this.vehicleExteriorColorID, this.vehicleInteriorColorID, this.vehicleInventoryId, this.vehicleMakeID, this.vehicleModelID, this.vehiclePackageIDs, this.vehicleTrimID, this.vehicleYearID, this.zipCode});

  factory Deal.fromJson(Map<String, dynamic> json) {
    return Deal(
      buyerID: json['buyerID'],
      canSavePayment: json['canSavePayment'],
      card_brand: json['card_brand'] != null ? Object.fromJson(
          json['card_brand']) : null,
      card_last4: json['card_last4'] != null ? Object.fromJson(
          json['card_last4']) : null,
      dealID: json['dealID'],
      dealTimeStamp: json['dealTimeStamp'],
      dealUcdDTOs: json['dealUcdDTOs'] != null ? (json['dealUcdDTOs'] as List)
          .map((i) => Object.fromJson(i))
          .toList() : null,
      dealerAccessoryIDs: json['dealerAccessoryIDs'] != null ? Object.fromJson(
          json['dealerAccessoryIDs']) : null,
      guestId: json['guestId'],
      initial: json['initial'],
      ip: json['ip'],
      isPackageNone: json['isPackageNone'],
      lastSubmissionTimeStamp: json['lastSubmissionTimeStamp'],
      loanType: json['loanType'],
      paymentLast4: json['paymentLast4'],
      paymentMethod: json['paymentMethod'],
      payment_intent_id: json['payment_intent_id'] != null ? Object.fromJson(
          json['payment_intent_id']) : null,
      payment_method_id: json['payment_method_id'] != null ? Object.fromJson(
          json['payment_method_id']) : null,
      price: json['price'],
      productID: json['productID'],
      promotionId: json['promotionId'] != null ? Object.fromJson(
          json['promotionId']) : null,
      reservedTimeStamp: json['reservedTimeStamp'],
      searchRadius: json['searchRadius'],
      seed: json['seed'],
      submissionCount: json['submissionCount'],
      timeZoneOffset: json['timeZoneOffset'],
      userID: json['userID'],
      userProfileID: json['userProfileID'],
      vehicleExteriorColorID: json['vehicleExteriorColorID'],
      vehicleInteriorColorID: json['vehicleInteriorColorID'] != null ? Object
          .fromJson(json['vehicleInteriorColorID']) : null,
      vehicleInventoryId: json['vehicleInventoryId'] != null ? Object.fromJson(
          json['vehicleInventoryId']) : null,
      vehicleMakeID: json['vehicleMakeID'],
      vehicleModelID: json['vehicleModelID'],
      vehiclePackageIDs: json['vehiclePackageIDs'] != null ? Object.fromJson(
          json['vehiclePackageIDs']) : null,
      vehicleTrimID: json['vehicleTrimID'],
      vehicleYearID: json['vehicleYearID'],
      zipCode: json['zipCode'],
    );
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['buyerID'] = this.buyerID;
    data['canSavePayment'] = this.canSavePayment;
    data['dealID'] = this.dealID;
    data['dealTimeStamp'] = this.dealTimeStamp;
    data['guestId'] = this.guestId;
    data['initial'] = this.initial;
    data['ip'] = this.ip;
    data['isPackageNone'] = this.isPackageNone;
    data['lastSubmissionTimeStamp'] = this.lastSubmissionTimeStamp;
    data['loanType'] = this.loanType;
    data['paymentLast4'] = this.paymentLast4;
    data['paymentMethod'] = this.paymentMethod;
    data['price'] = this.price;
    data['productID'] = this.productID;
    data['reservedTimeStamp'] = this.reservedTimeStamp;
    data['searchRadius'] = this.searchRadius;
    data['seed'] = this.seed;
    data['submissionCount'] = this.submissionCount;
    data['timeZoneOffset'] = this.timeZoneOffset;
    data['userID'] = this.userID;
    data['userProfileID'] = this.userProfileID;
    data['vehicleExteriorColorID'] = this.vehicleExteriorColorID;
    data['vehicleMakeID'] = this.vehicleMakeID;
    data['vehicleModelID'] = this.vehicleModelID;
    data['vehicleTrimID'] = this.vehicleTrimID;
    data['vehicleYearID'] = this.vehicleYearID;
    data['zipCode'] = this.zipCode;
    if (this.card_brand != null) {
      data['card_brand'] = this.card_brand.toJson();
    }
    if (this.card_last4 != null) {
      data['card_last4'] = this.card_last4.toJson();
    }
    if (this.dealUcdDTOs != null) {
      data['dealUcdDTOs'] = this.dealUcdDTOs.map((v) => v.toJson()).toList();
    }
    if (this.dealerAccessoryIDs != null) {
      data['dealerAccessoryIDs'] = this.dealerAccessoryIDs.toJson();
    }
    if (this.payment_intent_id != null) {
      data['payment_intent_id'] = this.payment_intent_id.toJson();
    }
    if (this.payment_method_id != null) {
      data['payment_method_id'] = this.payment_method_id.toJson();
    }
    if (this.promotionId != null) {
      data['promotionId'] = this.promotionId.toJson();
    }
    if (this.vehicleInteriorColorID != null) {
      data['vehicleInteriorColorID'] = this.vehicleInteriorColorID.toJson();
    }
    if (this.vehicleInventoryId != null) {
      data['vehicleInventoryId'] = this.vehicleInventoryId.toJson();
    }
    if (this.vehiclePackageIDs != null) {
      data['vehiclePackageIDs'] = this.vehiclePackageIDs.toJson();
    }
    return data;
  }
}