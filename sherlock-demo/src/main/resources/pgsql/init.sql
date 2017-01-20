/*
 Navicat Premium Data Transfer

 Source Server         : postgresql_localhost
 Source Server Type    : PostgreSQL
 Source Server Version : 90405
 Source Host           : localhost
 Source Database       : udb0
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90405
 File Encoding         : utf-8

 Date: 06/13/2016 17:50:39 PM
*/

-- ----------------------------
--  Table structure for t_item_0
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_item_0";
CREATE TABLE "public"."t_item_0" (
	"item_id" numeric NOT NULL,
	"order_id" numeric NOT NULL,
	"user_id" numeric NOT NULL,
	"status" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE);
ALTER TABLE "public"."t_item_0" OWNER TO "postgreuser";

-- ----------------------------
--  Records of t_item_0
-- ----------------------------
BEGIN;
INSERT INTO "public"."t_item_0" VALUES ('7', '27', '0', 'status-970843768');
INSERT INTO "public"."t_item_0" VALUES ('10', '30', '0', 'status-591580969');
INSERT INTO "public"."t_item_0" VALUES ('13', '33', '0', 'status-1192476597');
INSERT INTO "public"."t_item_0" VALUES ('16', '36', '0', 'status-130997777');
COMMIT;

-- ----------------------------
--  Table structure for t_item_1
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_item_1";
CREATE TABLE "public"."t_item_1" (
	"item_id" numeric NOT NULL,
	"order_id" numeric NOT NULL,
	"user_id" numeric NOT NULL,
	"status" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE);
ALTER TABLE "public"."t_item_1" OWNER TO "postgreuser";

-- ----------------------------
--  Records of t_item_1
-- ----------------------------
BEGIN;
INSERT INTO "public"."t_item_1" VALUES ('8', '28', '0', 'status1281627962');
INSERT INTO "public"."t_item_1" VALUES ('11', '31', '0', 'status-1874881577');
INSERT INTO "public"."t_item_1" VALUES ('14', '34', '0', 'status1467289651');
INSERT INTO "public"."t_item_1" VALUES ('17', '37', '0', 'status1664561888');
COMMIT;

-- ----------------------------
--  Table structure for t_item_2
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_item_2";
CREATE TABLE "public"."t_item_2" (
	"item_id" numeric NOT NULL,
	"order_id" numeric NOT NULL,
	"user_id" numeric NOT NULL,
	"status" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE);
ALTER TABLE "public"."t_item_2" OWNER TO "postgreuser";

-- ----------------------------
--  Records of t_item_2
-- ----------------------------
BEGIN;
INSERT INTO "public"."t_item_2" VALUES ('2', '23', '1', 'status-2058927716');
INSERT INTO "public"."t_item_2" VALUES ('9', '29', '0', 'status1484409515');
INSERT INTO "public"."t_item_2" VALUES ('12', '32', '0', 'status427320392');
INSERT INTO "public"."t_item_2" VALUES ('15', '35', '0', 'status-2033859517');
INSERT INTO "public"."t_item_2" VALUES ('18', '38', '0', 'status-1876869668');
COMMIT;

-- ----------------------------
--  Table structure for t_item_ext_0
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_item_ext_0";
CREATE TABLE "public"."t_item_ext_0" (
	"ext_id" numeric NOT NULL,
	"item_id" numeric NOT NULL,
	"status" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE);
ALTER TABLE "public"."t_item_ext_0" OWNER TO "postgreuser";

-- ----------------------------
--  Records of t_item_ext_0
-- ----------------------------
BEGIN;
INSERT INTO "public"."t_item_ext_0" VALUES ('4', '7', 'status-2043491080');
INSERT INTO "public"."t_item_ext_0" VALUES ('7', '10', 'status529543919');
INSERT INTO "public"."t_item_ext_0" VALUES ('10', '13', 'status-983731981');
INSERT INTO "public"."t_item_ext_0" VALUES ('13', '16', 'status-245452489');
COMMIT;

-- ----------------------------
--  Table structure for t_item_ext_1
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_item_ext_1";
CREATE TABLE "public"."t_item_ext_1" (
	"ext_id" numeric NOT NULL,
	"item_id" numeric NOT NULL,
	"status" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE);
ALTER TABLE "public"."t_item_ext_1" OWNER TO "postgreuser";

-- ----------------------------
--  Records of t_item_ext_1
-- ----------------------------
BEGIN;
INSERT INTO "public"."t_item_ext_1" VALUES ('5', '8', 'status350778158');
INSERT INTO "public"."t_item_ext_1" VALUES ('8', '11', 'status-284886720');
INSERT INTO "public"."t_item_ext_1" VALUES ('11', '14', 'status-1201482568');
INSERT INTO "public"."t_item_ext_1" VALUES ('14', '17', 'status1894720321');
COMMIT;

-- ----------------------------
--  Table structure for t_item_ext_2
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_item_ext_2";
CREATE TABLE "public"."t_item_ext_2" (
	"ext_id" numeric NOT NULL,
	"item_id" numeric NOT NULL,
	"status" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE);
ALTER TABLE "public"."t_item_ext_2" OWNER TO "postgreuser";

-- ----------------------------
--  Records of t_item_ext_2
-- ----------------------------
BEGIN;
INSERT INTO "public"."t_item_ext_2" VALUES ('3', '2', 'status104420805');
INSERT INTO "public"."t_item_ext_2" VALUES ('6', '9', 'status1136320205');
INSERT INTO "public"."t_item_ext_2" VALUES ('9', '12', 'status-1718334354');
INSERT INTO "public"."t_item_ext_2" VALUES ('12', '15', 'status-733558140');
INSERT INTO "public"."t_item_ext_2" VALUES ('15', '18', 'status733951758');
COMMIT;

-- ----------------------------
--  Table structure for t_order_0
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_order_0";
CREATE TABLE "public"."t_order_0" (
	"order_id" numeric NOT NULL,
	"user_id" numeric NOT NULL,
	"status" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE);
ALTER TABLE "public"."t_order_0" OWNER TO "postgreuser";

-- ----------------------------
--  Records of t_order_0
-- ----------------------------
BEGIN;
INSERT INTO "public"."t_order_0" VALUES ('12', '1', 'new');
INSERT INTO "public"."t_order_0" VALUES ('18', '1', 'new');
INSERT INTO "public"."t_order_0" VALUES ('21', '1', 'new');
INSERT INTO "public"."t_order_0" VALUES ('27', '0', 'status1955136834');
INSERT INTO "public"."t_order_0" VALUES ('30', '0', 'status-1869897438');
INSERT INTO "public"."t_order_0" VALUES ('33', '0', 'status-1648215998');
INSERT INTO "public"."t_order_0" VALUES ('36', '0', 'status-2116995573');
INSERT INTO "public"."t_order_0" VALUES ('39', '0', 'status-196322594');
INSERT INTO "public"."t_order_0" VALUES ('42', '0', 'status-901995625');
INSERT INTO "public"."t_order_0" VALUES ('45', '0', 'status752861562');
COMMIT;

-- ----------------------------
--  Table structure for t_order_1
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_order_1";
CREATE TABLE "public"."t_order_1" (
	"order_id" numeric NOT NULL,
	"user_id" numeric NOT NULL,
	"status" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE);
ALTER TABLE "public"."t_order_1" OWNER TO "postgreuser";

-- ----------------------------
--  Records of t_order_1
-- ----------------------------
BEGIN;
INSERT INTO "public"."t_order_1" VALUES ('13', '1', 'new');
INSERT INTO "public"."t_order_1" VALUES ('16', '1', 'new');
INSERT INTO "public"."t_order_1" VALUES ('28', '0', 'status538739525');
INSERT INTO "public"."t_order_1" VALUES ('31', '0', 'status-721556298');
INSERT INTO "public"."t_order_1" VALUES ('34', '0', 'status-1332951579');
INSERT INTO "public"."t_order_1" VALUES ('37', '0', 'status-1541211926');
INSERT INTO "public"."t_order_1" VALUES ('40', '0', 'status-252273631');
INSERT INTO "public"."t_order_1" VALUES ('43', '0', 'status815886354');
INSERT INTO "public"."t_order_1" VALUES ('46', '0', 'status549822887');
INSERT INTO "public"."t_order_1" VALUES ('19', '3', 'new');
COMMIT;

-- ----------------------------
--  Table structure for t_order_2
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_order_2";
CREATE TABLE "public"."t_order_2" (
	"order_id" numeric NOT NULL,
	"user_id" numeric NOT NULL,
	"status" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE);
ALTER TABLE "public"."t_order_2" OWNER TO "postgreuser";

-- ----------------------------
--  Records of t_order_2
-- ----------------------------
BEGIN;
INSERT INTO "public"."t_order_2" VALUES ('14', '1', 'new');
INSERT INTO "public"."t_order_2" VALUES ('17', '1', 'new');
INSERT INTO "public"."t_order_2" VALUES ('20', '1', 'new');
INSERT INTO "public"."t_order_2" VALUES ('23', '1', 'status-1064495912');
INSERT INTO "public"."t_order_2" VALUES ('29', '0', 'status1954627296');
INSERT INTO "public"."t_order_2" VALUES ('32', '0', 'status670257300');
INSERT INTO "public"."t_order_2" VALUES ('35', '0', 'status150627316');
INSERT INTO "public"."t_order_2" VALUES ('38', '0', 'status-111931557');
INSERT INTO "public"."t_order_2" VALUES ('41', '0', 'status-1026098682');
INSERT INTO "public"."t_order_2" VALUES ('44', '0', 'status1565284214');
INSERT INTO "public"."t_order_2" VALUES ('47', '0', 'status-916851753');
COMMIT;

-- ----------------------------
--  Primary key structure for table t_item_0
-- ----------------------------
ALTER TABLE "public"."t_item_0" ADD PRIMARY KEY ("item_id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table t_item_1
-- ----------------------------
ALTER TABLE "public"."t_item_1" ADD PRIMARY KEY ("item_id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table t_item_2
-- ----------------------------
ALTER TABLE "public"."t_item_2" ADD PRIMARY KEY ("item_id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table t_item_ext_0
-- ----------------------------
ALTER TABLE "public"."t_item_ext_0" ADD PRIMARY KEY ("ext_id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table t_item_ext_1
-- ----------------------------
ALTER TABLE "public"."t_item_ext_1" ADD PRIMARY KEY ("ext_id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table t_item_ext_2
-- ----------------------------
ALTER TABLE "public"."t_item_ext_2" ADD PRIMARY KEY ("ext_id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table t_order_0
-- ----------------------------
ALTER TABLE "public"."t_order_0" ADD PRIMARY KEY ("order_id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table t_order_1
-- ----------------------------
ALTER TABLE "public"."t_order_1" ADD PRIMARY KEY ("order_id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table t_order_2
-- ----------------------------
ALTER TABLE "public"."t_order_2" ADD PRIMARY KEY ("order_id") NOT DEFERRABLE INITIALLY IMMEDIATE;

