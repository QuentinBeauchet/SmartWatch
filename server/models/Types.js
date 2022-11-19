const { DataTypes, Model } = require("sequelize");

class Types extends Model {
  static init(connection) {
    return super.init(
      {
        id: {
          type: DataTypes.INTEGER,
          primaryKey: true,
          autoIncrement: true,
        },
        name: {
          type: DataTypes.STRING,
          length: 64,
          allowNull: false,
        },
        icon: {
          type: DataTypes.STRING,
          length: 512,
          allowNull: false,
        },
      },
      {
        sequelize: connection,
        tableName: "types",
        underscored: true,
        timestamps: false,
      }
    );
  }

  static associate(models) {
    this.hasMany(models.Events, {
      foreignKey: "type_id",
    });
  }
}

module.exports = {
  Types,
};
