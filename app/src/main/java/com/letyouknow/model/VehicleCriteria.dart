package
com.letyouknow.model

class VehicleCriteria {
  List<String> dealerAccessories;
  String exteriorColor;
  String interiorColor;
  String make;
  String model;
  List<String> packages;
  String trim;
  String year;

  VehicleCriteria(
      {this.dealerAccessories, this.exteriorColor, this.interiorColor, this.make, this.model, this.packages, this.trim, this.year});

  factory VehicleCriteria.fromJson(Map<String, dynamic> json) {
    return VehicleCriteria(
      dealerAccessories: json['dealerAccessories'] != null ? new List<
          String>.from(json['dealerAccessories']) : null,
      exteriorColor: json['exteriorColor'],
      interiorColor: json['interiorColor'],
      make: json['make'],
      model: json['model'],
      packages: json['packages'] != null ? new List<String>.from(
          json['packages']) : null,
      trim: json['trim'],
      year: json['year'],
    );
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['exteriorColor'] = this.exteriorColor;
    data['interiorColor'] = this.interiorColor;
    data['make'] = this.make;
    data['model'] = this.model;
    data['trim'] = this.trim;
    data['year'] = this.year;
    if (this.dealerAccessories != null) {
      data['dealerAccessories'] = this.dealerAccessories;
    }
    if (this.packages != null) {
      data['packages'] = this.packages;
    }
    return data;
  }
}