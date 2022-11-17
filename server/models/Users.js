const { DataTypes, Model } = require("sequelize");

class Users extends Model {
  static init(connection) {
    return super.init(
      {
        id: {
          type: DataTypes.INTEGER,
          primaryKey: true,
          autoIncrement: true,
        },
        device_id: {
          type: DataTypes.STRING,
          length: 16,
          allowNull: false,
        },
        name: {
          type: DataTypes.STRING,
          length: 128,
          allowNull: false,
        },
      },
      {
        sequelize: connection,
        tableName: "users",
        underscored: true,
        timestamps: false,
      }
    );
  }

  static associate(models) {
    this.hasMany(models.Events);
  }
}

module.exports = {
  Users,
};
