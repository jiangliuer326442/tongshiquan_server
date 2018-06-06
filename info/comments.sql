/*
Navicat MySQL Data Transfer

Source Server         : mysql_root
Source Server Version : 50713
Source Host           : localhost:3306
Source Database       : comments

Target Server Type    : MYSQL
Target Server Version : 50713
File Encoding         : 65001

Date: 2017-06-26 13:06:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for article_zan
-- ----------------------------
DROP TABLE IF EXISTS `article_zan`;
CREATE TABLE `article_zan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `article_id` int(11) NOT NULL COMMENT '文章id',
  `userid` int(11) NOT NULL COMMENT '用户id',
  `inserttime` timestamp NULL DEFAULT NULL,
  `updatetime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_zan` (`article_id`,`userid`) USING BTREE,
  KEY `article_zan_userid` (`userid`),
  CONSTRAINT `article_zan_article_id` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`),
  CONSTRAINT `article_zan_userid` FOREIGN KEY (`userid`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='文章点赞表';

-- ----------------------------
-- Records of article_zan
-- ----------------------------

-- ----------------------------
-- Table structure for articles
-- ----------------------------
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文章id',
  `plat_id` int(11) NOT NULL COMMENT '平台id',
  `article_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '平台文章标识（在该平台内保持唯一，如文章id，或文章分类与文章id的组合）',
  `zannum` int(11) NOT NULL DEFAULT '0' COMMENT '赞数量',
  `commentnum` int(11) NOT NULL DEFAULT '0' COMMENT '评论数量',
  `inserttime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `updatetime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `plat_id` (`plat_id`) USING BTREE,
  CONSTRAINT `plat_id` FOREIGN KEY (`plat_id`) REFERENCES `platforms` (`id`) ON DELETE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='文章表';

-- ----------------------------
-- Records of articles
-- ----------------------------
INSERT INTO `articles` VALUES ('1', '1', '1', '0', '1', '2017-06-23 15:36:45', '2017-06-23 15:36:45');
INSERT INTO `articles` VALUES ('2', '1', '5', '0', '1', '2017-06-24 21:02:14', '2017-06-24 21:02:14');
INSERT INTO `articles` VALUES ('3', '1', '7', '0', '1', '2017-06-24 22:28:06', '2017-06-24 22:28:06');
INSERT INTO `articles` VALUES ('4', '1', '9', '0', '1', '2017-06-25 17:24:40', '2017-06-25 17:24:40');
INSERT INTO `articles` VALUES ('5', '1', '12', '0', '2', '2017-06-25 17:53:44', '2017-06-25 17:53:44');
INSERT INTO `articles` VALUES ('6', '1', '14', '0', '2', '2017-06-25 20:29:48', '2017-06-25 20:29:48');
INSERT INTO `articles` VALUES ('7', '1', '16', '0', '1', '2017-06-26 10:24:48', '2017-06-26 10:24:48');
INSERT INTO `articles` VALUES ('8', '1', '13', '0', '0', '2017-06-26 11:53:28', '2017-06-26 11:53:28');

-- ----------------------------
-- Table structure for comment_zan
-- ----------------------------
DROP TABLE IF EXISTS `comment_zan`;
CREATE TABLE `comment_zan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_id` int(11) NOT NULL,
  `userid` int(11) NOT NULL,
  `inserttime` timestamp NULL DEFAULT NULL,
  `updatetime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `zan_unique` (`comment_id`,`userid`) USING BTREE,
  KEY `zan_userid` (`userid`),
  KEY `zan_comment_id` (`comment_id`),
  CONSTRAINT `zan_comment_id` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`),
  CONSTRAINT `zan_userid` FOREIGN KEY (`userid`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='评论点赞记录表';

-- ----------------------------
-- Records of comment_zan
-- ----------------------------

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `article_id` int(11) NOT NULL COMMENT '文章id',
  `user_id` int(11) NOT NULL COMMENT '评论的用户',
  `zannum` int(11) NOT NULL DEFAULT '0' COMMENT '赞数量',
  `comment` text COLLATE utf8_bin NOT NULL COMMENT '评论内容',
  `lastcomment_id` int(11) NOT NULL DEFAULT '0' COMMENT '上级评论id',
  `inserttime` timestamp NULL DEFAULT NULL,
  `updatetime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `article_id` (`article_id`) USING BTREE,
  KEY `user_id` (`user_id`),
  CONSTRAINT `article_id` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`),
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='评论记录表';

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES ('1', '1', '2', '0', 0x3C703EE6ACA2E8BF8EE68A80E69CAFE696B0E5908CE5ADA63C62722F3E3C62722F3E53656E742066726F6D206D79206950686F6E653C2F703E, '0', '2017-06-23 15:36:45', null);
INSERT INTO `comments` VALUES ('2', '2', '2', '0', 0x3C703EE6ACA2E8BF8E4149E696B0E5908CE5ADA63C62722F3E3C62722F3E20E58F91E887AAE68891E79A8420695061643C2F703E, '0', '2017-06-24 21:02:14', null);
INSERT INTO `comments` VALUES ('3', '3', '4', '0', 0x40E585A8E4BD93E68890E5919820E69CACE591A8E585AD36E69C883234E697A53132E782B93330E588862D3136E782B93330E58886E8BF9BE8A18CE5A4A7E58EA6E6B0B4E7AEB1E6B885E6B497E5B7A5E4BD9CEFBC8CE69A82E5819CE4BE9BE6B0B4EFBC8CE5A682E99C80E8A681E4BDBFE794A8E6B497E6898BE997B4EFBC8CE8AFB7E887B331E6A5BCE4BDBFE794A8EFBC8CE5A682E99C80E58AA0E78FADE8AFB7E5B0BDE9878FE981BFE5BC80E6ADA4E697B6E997B4E6AEB5E38082E8B0A2E8B0A2E9858DE59088E380825B656D5F315D, '0', '2017-06-24 22:28:06', null);
INSERT INTO `comments` VALUES ('4', '4', '1', '0', 0x3C703EE681A9EFBC8CE8AFB7E5A4A7E5AEB6E8B88AE8B783E68AA5E5908DEFBC8CE588B0E697B6E58099E697A9E782B9E69DA5E585ACE58FB8EFBC8CE68891E4BBACE59D90E5A4A7E5B7B4E58EBBEFBC8CE58C97E4BAACE8BF99E8BEB9E79A84E59BA2E5BBBAE6B4BBE58AA8E697B6E997B4E8BF98E6B2A1E7A1AEE5AE9AE5A5BDEFBC8CE4B88DE8BF87E882AFE5AE9AE698AFE69C89E79A84E38082E68980E4BBA5EFBC8CE58C97E4BAACE8BF99E8BEB9E79A84E69C8BE58F8BE4BBACE4B99FE588ABE5A4B1E69C9BE5958AEFBD9E3C2F703E, '0', '2017-06-25 17:24:40', null);
INSERT INTO `comments` VALUES ('5', '5', '1', '0', 0x3C703EE68A80E69CAFE983A8EFBC9A3C2F703E3C703E3C696D67207372633D22687474703A2F2F7374617469632E636F6D70616E79636C75622E636E2F61727469636C655F3230313730363235313735323234393334372E706E6722207469746C653D222220616C743D22222F3E3C2F703E, '0', '2017-06-25 17:53:12', null);
INSERT INTO `comments` VALUES ('6', '5', '5', '0', 0x3C703EE69F8FE69DBE353620E4B89CE696B9363520E9AD8FE788BD36353C2F703E, '0', '2017-06-25 17:53:44', null);
INSERT INTO `comments` VALUES ('7', '6', '5', '0', 0x3C703E3C696D67207372633D22687474703A2F2F63646E2E636F6D70616E79636C75622E636E2F6C6962726172792F75656469742F7468656D65732F64656661756C742F696D616765732F7370616365722E676966222F3EE8B0A2E8B0A2EFBC8CE5A4A7E5AEB6E5A5BDEFBC8CE5A4A7E5AEB6E5A5BD7E7E3C62722F3E3C2F703E3C703E3C62722F3E3C2F703E, '0', '2017-06-25 20:26:12', null);
INSERT INTO `comments` VALUES ('8', '6', '6', '0', 0x3C703EE6ACA2E8BF8E3C696D67207372633D22687474703A2F2F7374617469632E636F6D70616E79636C75622E636E2F61727469636C655F3230313730363235323032393432363933392E6A706722207469746C653D222220616C743D22222F3E3C2F703E, '0', '2017-06-25 20:29:48', null);
INSERT INTO `comments` VALUES ('9', '7', '2', '0', 0xE8B0A2E8B0A24A616D6573E79A84E4B8A5E6A0BCE8BFBDE8B4A3E5928CE794A8E4BA8EE689BFE68B85E380820A312E2020E5AE9DE5AE9DE6A091E79A84E9AB98E5B1826C656164657273E5B0B1E698AFE8A681E69C89E8BF99E7A78DE98187E588B0E997AEE9A298EFBC8C20E8A7A3E586B3E997AEE9A298EFBC8C20E5B9B6E794A8E4BA8EE689BFE68B85E79A84E7B2BEE7A59EE380820A322E20E5AE9DE5AE9DE6A091E79A84E5B7A5E7A88BE5B888E69687E58C96E38081E4BAA7E59381E69687E58C96E5BF85E9A1BBE59CA84A616D6573E79A84E5B8A6E9A286E4B88BE5BE97E4BBA5E7BBA7E7BBADE58AA0E5BCBAE380822020E8BF99E698AFE68891E4BBACE7BBA7E7BBADE79A84E68890E58A9FE585B3E994AEE38082, '0', '2017-06-26 10:24:48', null);

-- ----------------------------
-- Table structure for platform_users
-- ----------------------------
DROP TABLE IF EXISTS `platform_users`;
CREATE TABLE `platform_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `platformid` int(11) NOT NULL,
  `userid` int(11) NOT NULL,
  `inserttime` timestamp NULL DEFAULT NULL,
  `updatetime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `username` char(50) COLLATE utf8_bin NOT NULL COMMENT '手机号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`platformid`,`userid`) USING HASH,
  KEY `userid` (`userid`) USING HASH,
  CONSTRAINT `platformid` FOREIGN KEY (`platformid`) REFERENCES `platforms` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `userid` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='平台用户表';

-- ----------------------------
-- Records of platform_users
-- ----------------------------
INSERT INTO `platform_users` VALUES ('1', '1', '1', '2017-06-23 14:29:09', '2017-06-23 14:45:24', '赵鑫');
INSERT INTO `platform_users` VALUES ('2', '1', '2', '2017-06-23 14:40:38', '2017-06-23 14:48:57', 'Allen Wang');
INSERT INTO `platform_users` VALUES ('3', '1', '3', '2017-06-24 18:59:03', null, '15811266682');
INSERT INTO `platform_users` VALUES ('4', '1', '4', '2017-06-24 22:15:34', '2017-06-24 22:24:31', '【群主】徐慧');
INSERT INTO `platform_users` VALUES ('5', '1', '5', '2017-06-25 17:47:38', '2017-06-25 17:49:11', '包子');
INSERT INTO `platform_users` VALUES ('6', '1', '6', '2017-06-25 18:02:51', null, '18502602584');
INSERT INTO `platform_users` VALUES ('7', '1', '7', '2017-06-25 20:35:21', '2017-06-25 20:38:40', '村长');
INSERT INTO `platform_users` VALUES ('8', '1', '8', '2017-06-26 10:02:51', '2017-06-26 10:03:55', 'Michael');

-- ----------------------------
-- Table structure for platforms
-- ----------------------------
DROP TABLE IF EXISTS `platforms`;
CREATE TABLE `platforms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `web_id` int(11) NOT NULL COMMENT '域名id',
  `plat_form_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '网站平台标识（在该网站内保持唯一，例如平台id或平台名称）',
  `articlenums` int(11) NOT NULL DEFAULT '0' COMMENT '文章数量',
  `usernums` int(11) NOT NULL DEFAULT '0' COMMENT '用户数量',
  `auth` char(32) COLLATE utf8_bin NOT NULL,
  `inserttime` timestamp NULL DEFAULT NULL,
  `updatetime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth` (`web_id`,`auth`) USING HASH,
  UNIQUE KEY `plat_auth` (`web_id`,`plat_form_id`) USING HASH,
  CONSTRAINT `web_id` FOREIGN KEY (`web_id`) REFERENCES `webs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='域名下子账户列表';

-- ----------------------------
-- Records of platforms
-- ----------------------------
INSERT INTO `platforms` VALUES ('1', '1', '02caca650bc3b2d6a5fbbb19b9a76c5d', '8', '8', '425C9BEDAE257ACA879FFA6F88BC8549', '2017-06-23 14:29:09', '2017-06-26 11:52:58');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `phone` char(11) COLLATE utf8_bin NOT NULL COMMENT '用户手机号',
  `inserttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updatetime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone` (`phone`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户表';

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('1', '18217503693', '2017-06-23 14:29:09', null);
INSERT INTO `users` VALUES ('2', '13337778966', '2017-06-23 14:40:38', null);
INSERT INTO `users` VALUES ('3', '15811266682', '2017-06-24 18:59:03', null);
INSERT INTO `users` VALUES ('4', '13524925280', '2017-06-24 22:15:34', null);
INSERT INTO `users` VALUES ('5', '18616784330', '2017-06-25 17:47:38', null);
INSERT INTO `users` VALUES ('6', '18502602584', '2017-06-25 18:02:51', null);
INSERT INTO `users` VALUES ('7', '15201921007', '2017-06-25 20:35:21', null);
INSERT INTO `users` VALUES ('8', '18611750351', '2017-06-26 10:02:51', null);

-- ----------------------------
-- Table structure for webs
-- ----------------------------
DROP TABLE IF EXISTS `webs`;
CREATE TABLE `webs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` char(50) COLLATE utf8_bin NOT NULL COMMENT '注册的网站url',
  `platnums` int(11) NOT NULL DEFAULT '0' COMMENT '域名的平台数量',
  `auth` char(32) COLLATE utf8_bin NOT NULL COMMENT 'auth编码',
  `inserttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatetime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `url` (`url`) USING HASH,
  KEY `auth` (`auth`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='允许调用API的网站列表';

-- ----------------------------
-- Records of webs
-- ----------------------------
INSERT INTO `webs` VALUES ('1', 'www.companyclub.cn', '1', '3ADBB9BBE346E9E810809D19977D4418', '2016-09-01 10:24:26', '2017-06-23 14:29:09');

-- ----------------------------
-- View structure for V_ARTICLE_COMMENTS
-- ----------------------------
DROP VIEW IF EXISTS `V_ARTICLE_COMMENTS`;
CREATE ALGORITHM=TEMPTABLE DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `V_ARTICLE_COMMENTS` AS select `c`.`id` AS `comment_id`,`c`.`zannum` AS `comment_zannum`,`c`.`comment` AS `comments`,`c`.`inserttime` AS `comment_inserttime`,`c`.`updatetime` AS `comment_updatetime`,`c`.`lastcomment_id` AS `lastcomment_id`,`u`.`phone` AS `user_phone`,`pu`.`username` AS `user_name`,`a`.`id` AS `aid`,`a`.`article_id` AS `article_id`,`a`.`zannum` AS `article_zannum`,`a`.`commentnum` AS `article_commentnum`,`a`.`inserttime` AS `article_inserttime`,`a`.`updatetime` AS `article_updatetime`,`a`.`plat_id` AS `plat_id`,ifnull(`c2`.`comment`,'') AS `to_content`,ifnull(`pu2`.`username`,'') AS `to_username` from (((((`comments` `c` left join `articles` `a` on((`c`.`article_id` = `a`.`id`))) left join `comments` `c2` on((`c`.`lastcomment_id` = `c2`.`id`))) left join `platform_users` `pu2` on(((`c2`.`user_id` = `pu2`.`userid`) and (`pu2`.`platformid` = `a`.`plat_id`)))) left join `users` `u` on((`c`.`user_id` = `u`.`id`))) left join `platform_users` `pu` on((`c`.`user_id` = `pu`.`userid`))) ;

-- ----------------------------
-- View structure for V_PLATFORM_USERS
-- ----------------------------
DROP VIEW IF EXISTS `V_PLATFORM_USERS`;
CREATE ALGORITHM=TEMPTABLE DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `V_PLATFORM_USERS` AS select `w`.`id` AS `id`,`w`.`url` AS `url`,`pu`.`platformid` AS `platformid`,`p`.`plat_form_id` AS `plat_form_id`,`pu`.`userid` AS `userid`,`u`.`phone` AS `phone`,`p`.`auth` AS `pauth`,`w`.`auth` AS `wauth` from (((`platform_users` `pu` left join `platforms` `p` on((`pu`.`platformid` = `p`.`id`))) left join `users` `u` on((`pu`.`userid` = `u`.`id`))) left join `webs` `w` on((`w`.`id` = `p`.`web_id`))) ;
DROP TRIGGER IF EXISTS `articleaddzan`;
DELIMITER ;;
CREATE TRIGGER `articleaddzan` AFTER INSERT ON `article_zan` FOR EACH ROW update articles set zannum = zannum+1 where id = new.article_id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `articlermvzan`;
DELIMITER ;;
CREATE TRIGGER `articlermvzan` AFTER DELETE ON `article_zan` FOR EACH ROW update articles set zannum = zannum-1 where id = old.article_id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `addarticle`;
DELIMITER ;;
CREATE TRIGGER `addarticle` AFTER INSERT ON `articles` FOR EACH ROW update platforms set articlenums = articlenums+1 where id = new.plat_id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `beforedeletearticle`;
DELIMITER ;;
CREATE TRIGGER `beforedeletearticle` BEFORE DELETE ON `articles` FOR EACH ROW delete from comments where article_id = old.id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `deletearticle`;
DELIMITER ;;
CREATE TRIGGER `deletearticle` AFTER DELETE ON `articles` FOR EACH ROW update platforms set articlenums = articlenums-1 where id = old.plat_id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `commentaddzan`;
DELIMITER ;;
CREATE TRIGGER `commentaddzan` AFTER INSERT ON `comment_zan` FOR EACH ROW update comments set zannum = zannum+1 where id = new.comment_id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `commentrmvzan`;
DELIMITER ;;
CREATE TRIGGER `commentrmvzan` AFTER DELETE ON `comment_zan` FOR EACH ROW update comments set zannum = zannum-1 where id = old.comment_id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `insertdata`;
DELIMITER ;;
CREATE TRIGGER `insertdata` AFTER INSERT ON `comments` FOR EACH ROW update articles set commentnum = commentnum + 1 where id = new.article_id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `deletedata`;
DELIMITER ;;
CREATE TRIGGER `deletedata` AFTER DELETE ON `comments` FOR EACH ROW update articles set commentnum = commentnum - 1 where id = old.article_id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `afterinsertuser`;
DELIMITER ;;
CREATE TRIGGER `afterinsertuser` AFTER INSERT ON `platform_users` FOR EACH ROW update platforms set usernums = usernums + 1 where id = new.platformid
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `afterdeleteplatuser`;
DELIMITER ;;
CREATE TRIGGER `afterdeleteplatuser` AFTER DELETE ON `platform_users` FOR EACH ROW update platforms set usernums = usernums - 1 where id = old.platformid
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `afterinsert`;
DELIMITER ;;
CREATE TRIGGER `afterinsert` AFTER INSERT ON `platforms` FOR EACH ROW update webs set platnums = platnums + 1 where id = new.web_id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `beforedelete_platform`;
DELIMITER ;;
CREATE TRIGGER `beforedelete_platform` BEFORE DELETE ON `platforms` FOR EACH ROW begin
delete from articles where plat_id = old.id;
delete from platform_users where platformid = old.id;
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `afterdelete`;
DELIMITER ;;
CREATE TRIGGER `afterdelete` AFTER DELETE ON `platforms` FOR EACH ROW update webs set platnums = platnums - 1 where id = old.web_id
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `beforedeleteuser`;
DELIMITER ;;
CREATE TRIGGER `beforedeleteuser` BEFORE DELETE ON `users` FOR EACH ROW begin
delete from comments where user_id = old.id;
delete from platform_users where userid = old.id;
end
;;
DELIMITER ;
