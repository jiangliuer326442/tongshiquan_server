/*
Navicat MySQL Data Transfer

Source Server         : rubyjsp_dev
Source Server Version : 50718
Source Host           : 172.17.0.3:3221
Source Database       : comments

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2018-06-05 18:31:43
*/

SET FOREIGN_KEY_CHECKS=0;

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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='文章表';

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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='评论记录表';

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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='域名下子账户列表';

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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='平台用户表';

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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户表';

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
-- View structure for v_article_comments
-- ----------------------------
DROP VIEW IF EXISTS `v_article_comments`;
CREATE ALGORITHM=TEMPTABLE DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `v_article_comments` AS select `c`.`id` AS `comment_id`,`c`.`zannum` AS `comment_zannum`,`c`.`comment` AS `comments`,`c`.`inserttime` AS `comment_inserttime`,`c`.`updatetime` AS `comment_updatetime`,`c`.`lastcomment_id` AS `lastcomment_id`,`u`.`phone` AS `user_phone`,`pu`.`username` AS `user_name`,`a`.`id` AS `aid`,`a`.`article_id` AS `article_id`,`a`.`zannum` AS `article_zannum`,`a`.`commentnum` AS `article_commentnum`,`a`.`inserttime` AS `article_inserttime`,`a`.`updatetime` AS `article_updatetime`,`a`.`plat_id` AS `plat_id`,ifnull(`c2`.`comment`,'') AS `to_content`,ifnull(`pu2`.`username`,'') AS `to_username` from (((((`comments` `c` left join `articles` `a` on((`c`.`article_id` = `a`.`id`))) left join `comments` `c2` on((`c`.`lastcomment_id` = `c2`.`id`))) left join `platform_users` `pu2` on(((`c2`.`user_id` = `pu2`.`userid`) and (`pu2`.`platformid` = `a`.`plat_id`)))) left join `users` `u` on((`c`.`user_id` = `u`.`id`))) left join `platform_users` `pu` on((`c`.`user_id` = `pu`.`userid`))) ;

-- ----------------------------
-- View structure for v_platform_users
-- ----------------------------
DROP VIEW IF EXISTS `v_platform_users`;
CREATE ALGORITHM=TEMPTABLE DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `v_platform_users` AS select `w`.`id` AS `id`,`w`.`url` AS `url`,`pu`.`platformid` AS `platformid`,`p`.`plat_form_id` AS `plat_form_id`,`pu`.`userid` AS `userid`,`u`.`phone` AS `phone`,`p`.`auth` AS `pauth`,`w`.`auth` AS `wauth` from (((`platform_users` `pu` left join `platforms` `p` on((`pu`.`platformid` = `p`.`id`))) left join `users` `u` on((`pu`.`userid` = `u`.`id`))) left join `webs` `w` on((`w`.`id` = `p`.`web_id`))) ;
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
DROP TRIGGER IF EXISTS `beforedeleteuser`;
DELIMITER ;;
CREATE TRIGGER `beforedeleteuser` BEFORE DELETE ON `users` FOR EACH ROW begin
delete from comments where user_id = old.id;
delete from platform_users where userid = old.id;
end
;;
DELIMITER ;
