drop table t_item_0;
CREATE TABLE t_item_0
  (
    item_id   NUMBER(10) CONSTRAINT t_item_0_id_demo NOT NULL,
    order_id  NUMBER(10) CONSTRAINT t_item_0_order_id_demo NOT NULL,
    "user_id" NUMBER(10) CONSTRAINT t_item_0_user_id_demo NOT NULL,
    "status"  VARCHAR2(50),
    CONSTRAINT item_0_c_id_pk_demo PRIMARY KEY (item_id )
  );
INSERT INTO t_item_0 VALUES
  ('7', '27', '0', 'status-970843768'
  );
INSERT INTO t_item_0 VALUES
  ('10', '30', '0', 'status-591580969'
  );
INSERT INTO t_item_0 VALUES
  ('13', '33', '0', 'status-1192476597'
  );
INSERT INTO t_item_0 VALUES
  ('16', '36', '0', 'status-130997777'
  );
commit;
select * from itest;
drop table t_item_1;
CREATE TABLE t_item_1
  (
    item_id   NUMBER(10) CONSTRAINT t_item_1_id_demo NOT NULL,
    order_id  NUMBER(10) CONSTRAINT t_item_1_order_id_demo NOT NULL,
    "user_id" NUMBER(10) CONSTRAINT t_item_1_user_id_demo NOT NULL,
    "status"  VARCHAR2(50),
    CONSTRAINT item_1_c_id_pk_demo PRIMARY KEY (item_id )
  );
INSERT INTO t_item_1 VALUES
  ('8', '28', '0', 'status1281627962'
  );
INSERT INTO t_item_1 VALUES
  ('11', '31', '0', 'status-1874881577'
  );
INSERT INTO t_item_1 VALUES
  ('14', '34', '0', 'status1467289651'
  );
INSERT INTO t_item_1 VALUES
  ('17', '37', '0', 'status1664561888'
  );
commit;

drop table t_item_2;
CREATE TABLE t_item_2
  (
    item_id   NUMBER(10) CONSTRAINT t_item_2_id_demo NOT NULL,
    order_id  NUMBER(10) CONSTRAINT t_item_2_order_id_demo NOT NULL,
    "user_id" NUMBER(10) CONSTRAINT t_item_2_user_id_demo NOT NULL,
    "status"  VARCHAR2(50),
    CONSTRAINT item_2_c_id_pk_demo PRIMARY KEY (item_id )
  );
INSERT INTO t_item_2 VALUES
  ('2', '23', '1', 'status-2058927716'
  );
INSERT INTO t_item_2 VALUES
  ('9', '29', '0', 'status1484409515'
  );
INSERT INTO t_item_2 VALUES
  ('12', '32', '0', 'status427320392'
  );
INSERT INTO t_item_2 VALUES
  ('15', '35', '0', 'status-2033859517'
  );
INSERT INTO t_item_2 VALUES
  ('18', '38', '0', 'status-1876869668'
  );
commit;

drop table t_item_ext_0;
CREATE TABLE t_item_ext_0
  (
    ext_id  NUMBER(10) CONSTRAINT t_item_ext_0_id_demo NOT NULL,
    item_id NUMBER(10) CONSTRAINT t_item_ext_0_item_id_demo NOT NULL,
    status  VARCHAR2(50),
    CONSTRAINT item_ext_0_c_id_pk_demo PRIMARY KEY (ext_id )
  );
INSERT INTO t_item_ext_0 VALUES
  ('4', '7', 'status-2043491080'
  );
INSERT INTO t_item_ext_0 VALUES
  ('7', '10', 'status529543919'
  );
INSERT INTO t_item_ext_0 VALUES
  ('10', '13', 'status-983731981'
  );
INSERT INTO t_item_ext_0 VALUES
  ('13', '16', 'status-245452489'
  );
COMMIT;

drop table t_item_ext_1;
CREATE TABLE t_item_ext_1
  (
    ext_id  NUMBER(10) CONSTRAINT t_item_ext_1_id_demo NOT NULL,
    item_id NUMBER(10) CONSTRAINT t_item_ext_1_item_id_demo NOT NULL,
    status  VARCHAR2(50),
    CONSTRAINT item_ext_1_c_id_pk_demo PRIMARY KEY (ext_id )
  );
INSERT INTO t_item_ext_1 VALUES
  ('5', '8', 'status350778158'
  );
INSERT INTO t_item_ext_1 VALUES
  ('8', '11', 'status-284886720'
  );
INSERT INTO t_item_ext_1 VALUES
  ('11', '14', 'status-1201482568'
  );
INSERT INTO t_item_ext_1 VALUES
  ('14', '17', 'status1894720321'
  );
COMMIT;
drop table t_item_ext_2;
CREATE TABLE t_item_ext_2
  (
    ext_id  NUMBER(10) CONSTRAINT t_item_ext_2_id_demo NOT NULL,
    item_id NUMBER(10) CONSTRAINT t_item_ext_2_item_id_demo NOT NULL,
    status  VARCHAR2(50),
    CONSTRAINT item_ext_2_c_id_pk_demo PRIMARY KEY (ext_id )
  );
INSERT INTO t_item_ext_2 VALUES
  ('3', '2', 'status104420805'
  );
INSERT INTO t_item_ext_2 VALUES
  ('6', '9', 'status1136320205'
  );
INSERT INTO t_item_ext_2 VALUES
  ('9', '12', 'status-1718334354'
  );
INSERT INTO t_item_ext_2 VALUES
  ('12', '15', 'status-733558140'
  );
INSERT INTO t_item_ext_2 VALUES
  ('15', '18', 'status733951758'
  );
COMMIT;
DROP TABLE t_order_0;
CREATE TABLE t_order_0
  (
    order_id NUMBER(10) CONSTRAINT t_order_0_id_demo NOT NULL,
    user_id  NUMBER(10) CONSTRAINT t_order_0_user_id_demo NOT NULL,
    status   VARCHAR2(50),
    CONSTRAINT order_0_c_id_pk_demo PRIMARY KEY (order_id )
  );
-- ----------------------------
--  Records of t_order_0
-- ----------------------------

  INSERT INTO t_order_0 VALUES
    ('12', '1', 'new'
    );
  INSERT INTO t_order_0 VALUES
    ('18', '1', 'new'
    );
  INSERT INTO t_order_0 VALUES
    ('21', '1', 'new'
    );
  INSERT INTO t_order_0 VALUES
    ('27', '0', 'status1955136834'
    );
  INSERT INTO t_order_0 VALUES
    ('30', '0', 'status-1869897438'
    );
  INSERT INTO t_order_0 VALUES
    ('33', '0', 'status-1648215998'
    );
  INSERT INTO t_order_0 VALUES
    ('36', '0', 'status-2116995573'
    );
  INSERT INTO t_order_0 VALUES
    ('39', '0', 'status-196322594'
    );
  INSERT INTO t_order_0 VALUES
    ('42', '0', 'status-901995625'
    );
  INSERT INTO t_order_0 VALUES
    ('45', '0', 'status752861562'
    );
  COMMIT;
  
  DROP TABLE t_order_1;
  CREATE TABLE t_order_1
    (
      order_id NUMBER(10) CONSTRAINT t_order_1_id_demo NOT NULL,
      user_id  NUMBER(10) CONSTRAINT t_order_1_user_id_demo NOT NULL,
      status   VARCHAR2(50),
      CONSTRAINT order_1_c_id_pk_demo PRIMARY KEY (order_id )
    );
  -- ----------------------------
  --  Records of t_order_0
  -- ----------------------------

    INSERT INTO t_order_1 VALUES
      ('13', '1', 'new'
      );
    INSERT INTO t_order_1 VALUES
      ('16', '1', 'new'
      );
    INSERT INTO t_order_1 VALUES
      ('28', '0', 'status538739525'
      );
    INSERT INTO t_order_1 VALUES
      ('31', '0', 'status-721556298'
      );
    INSERT INTO t_order_1 VALUES
      ('34', '0', 'status-1332951579'
      );
    INSERT INTO t_order_1 VALUES
      ('37', '0', 'status-1541211926'
      );
    INSERT INTO t_order_1 VALUES
      ('40', '0', 'status-252273631'
      );
    INSERT INTO t_order_1 VALUES
      ('43', '0', 'status815886354'
      );
    INSERT INTO t_order_1 VALUES
      ('46', '0', 'status549822887'
      );
    INSERT INTO t_order_1 VALUES
      ('19', '3', 'new'
      );
    COMMIT;
    
    DROP TABLE t_order_2;
    CREATE TABLE t_order_2
      (
        order_id NUMBER(10) CONSTRAINT t_order_2_id_demo NOT NULL,
        user_id  NUMBER(10) CONSTRAINT t_order_2_user_id_demo NOT NULL,
        status   VARCHAR2(50),
        CONSTRAINT order_2_c_id_pk_demo PRIMARY KEY (order_id )
      );
    -- ----------------------------
    --  Records of t_order_0
    -- ----------------------------

      INSERT INTO t_order_2 VALUES
        ('14', '1', 'new'
        );
      INSERT INTO t_order_2 VALUES
        ('17', '1', 'new'
        );
      INSERT INTO t_order_2 VALUES
        ('20', '1', 'new'
        );
      INSERT INTO t_order_2 VALUES
        ('23', '1', 'status-1064495912'
        );
      INSERT INTO t_order_2 VALUES
        ('29', '0', 'status1954627296'
        );
      INSERT INTO t_order_2 VALUES
        ('32', '0', 'status670257300'
        );
      INSERT INTO t_order_2 VALUES
        ('35', '0', 'status150627316'
        );
      INSERT INTO t_order_2 VALUES
        ('38', '0', 'status-111931557'
        );
      INSERT INTO t_order_2 VALUES
        ('41', '0', 'status-1026098682'
        );
      INSERT INTO t_order_2 VALUES
        ('44', '0', 'status1565284214'
        );
      INSERT INTO t_order_2 VALUES
        ('47', '0', 'status-916851753'
        );
      COMMIT;