package
com.letyouknow.model

class VehicleInStockCheckInput {
  Object accessoryList;
  int exteriorColorId;
  int interiorColorId;
  int makeId;
  int modelId;
  Object packageList;
  int product;
  int searchRadius;
  int trimId;
  int yearId;
  String zipcode;

  VehicleInStockCheckInput(
      {this.accessoryList, this.exteriorColorId, this.interiorColorId, this.makeId, this.modelId, this.packageList, this.product, this.searchRadius, this.trimId, this.yearId, this.zipcode});

  factory VehicleInStockCheckInput.fromJson(Map<String, dynamic> json) {
    return VehicleInStockCheckInput(
      accessoryList: json['accessoryList'] != null ? Object.fromJson(
          json['accessoryList']) : null,
      exteriorColorId: json['exteriorColorId'],
      interiorColorId: json['interiorColorId'],
      makeId: json['makeId'],
      modelId: json['modelId'],
      packageList: json['packageList'] != null ? Object.fromJson(
          json['packageList']) : null,
      product: json['product'],
      searchRadius: json['searchRadius'],
      trimId: json['trimId'],
      yearId: json['yearId'],
      zipcode: json['zipcode'],
    );
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['exteriorColorId'] = this.exteriorColorId;
    data['interiorColorId'] = this.interiorColorId;
    data['makeId'] = this.makeId;
    data['modelId'] = this.modelId;
    data['product'] = this.product;
    data['searchRadius'] = this.searchRadius;
    data['trimId'] = this.trimId;
    data['yearId'] = this.yearId;
    data['zipcode'] = this.zipcode;
    if (this.accessoryList != null) {
      data['accessoryList'] = this.accessoryList.toJson();
    }
    if (this.packageList != null) {
      data['packageList'] = this.packageList.toJson();
    }
    return data;
  }
}