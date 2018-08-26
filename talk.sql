/*
 Navicat MySQL Data Transfer

 Source Server         : talk
 Source Server Type    : MySQL
 Source Server Version : 50641
 Source Host           : localhost
 Source Database       : talk

 Target Server Type    : MySQL
 Target Server Version : 50641
 File Encoding         : utf-8

 Date: 08/26/2018 11:34:30 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `clips`
-- ----------------------------
DROP TABLE IF EXISTS `clips`;
CREATE TABLE `clips` (
  `clips_id` varchar(32) NOT NULL,
  `clips_name` varchar(125) DEFAULT NULL,
  `clips_cover` varchar(256) DEFAULT NULL,
  `clips_addr` varchar(256) DEFAULT NULL,
  `cn` varchar(125) DEFAULT NULL,
  `en` varchar(256) DEFAULT NULL,
  `video_id` varchar(32) DEFAULT NULL,
  `lang_type` int(11) DEFAULT '0' COMMENT '0 中文 1英文',
  `order_num` int(11) DEFAULT NULL,
  `clips_md5` varchar(32) DEFAULT NULL COMMENT 'videoid 与file name的32位MD5',
  PRIMARY KEY (`clips_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `music`
-- ----------------------------
DROP TABLE IF EXISTS `music`;
CREATE TABLE `music` (
  `music_id` varchar(32) NOT NULL,
  `srt_path` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`music_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tv`
-- ----------------------------
DROP TABLE IF EXISTS `tv`;
CREATE TABLE `tv` (
  `tv_id` varchar(32) NOT NULL,
  `tv_name` text,
  `type` int(11) DEFAULT '0' COMMENT '0电视剧 1，电影',
  `lang_type` int(11) DEFAULT '0' COMMENT '0汉语，1英语',
  PRIMARY KEY (`tv_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` varchar(32) CHARACTER SET utf8 NOT NULL,
  `user_name` varchar(16) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `video`
-- ----------------------------
DROP TABLE IF EXISTS `video`;
CREATE TABLE `video` (
  `video_id` varchar(32) NOT NULL,
  `eps` int(11) DEFAULT '1' COMMENT '集数',
  `srt` varchar(125) DEFAULT NULL COMMENT '字幕路径',
  `tv_id` varchar(32) DEFAULT NULL,
  `video_name` varchar(125) DEFAULT NULL,
  PRIMARY KEY (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
