const { DataTypes, Model } = require("sequelize");

class Events extends Model {
  static init(connection) {
    return super.init(
      {
        id: {
          type: DataTypes.INTEGER,
          primaryKey: true,
          autoIncrement: true,
        },
        type_id: {
          type: DataTypes.INTEGER,
        },
        user_id: {
          type: DataTypes.INTEGER,
        },
        latitude: {
          type: DataTypes.DECIMAL,
          length: 10,
          precision: 7,
          allowNull: false,
        },
        longitude: {
          type: DataTypes.DECIMAL,
          length: 10,
          precision: 7,
          allowNull: false,
        },
        date: {
          type: DataTypes.DATE,
          allowNull: false,
        },
        comment: {
          type: DataTypes.TEXT,
          allowNull: true,
        },
      },
      {
        sequelize: connection,
        tableName: "events",
        underscored: true,
        timestamps: false,
      }
    );
  }

  static associate(models) {
    this.belongsTo(models.Users, {
      foreignKey: "id",
    });
    this.belongsTo(models.Types, {
      foreignKey: "id",
    });
  }
}

module.exports = {
  Events,
};
