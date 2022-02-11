/*
 Navicat Premium Data Transfer

 Source Server         : zsk
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : 127.0.0.1:3306
 Source Schema         : video_db

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : 65001

 Date: 21/02/2020 18:05:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for video_info
-- ----------------------------
DROP TABLE IF EXISTS `video_info`;
CREATE TABLE `video_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父级id，如果为顶级，就为0',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `level` int(10) NULL DEFAULT NULL COMMENT '位置类型：0-根，1-楼层，2-具体位置',
  `orig_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '源视频地址',
  `show_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转码后的视频地址',
  `is_del` int(10) NULL DEFAULT NULL COMMENT '是否删除：0-未删除，1-已经删除',
  `type` int(10) NULL DEFAULT NULL COMMENT '转码方式，无特殊要求只需要一种，0',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of video_info
-- ----------------------------
INSERT INTO `video_info` VALUES (1, 0, '主目录', 0, 'http1', 'https1', 0, NULL, '2020-02-19 20:38:31', '2020-02-19 20:38:33');
INSERT INTO `video_info` VALUES (2, 1, '一级目录1', 1, 'http2', 'https2', 0, NULL, '2020-02-19 20:38:58', '2020-02-19 20:39:01');
INSERT INTO `video_info` VALUES (3, 1, '一级目录2', 1, 'http3', 'https3', 0, NULL, '2020-02-19 20:39:27', '2020-02-19 20:39:29');
INSERT INTO `video_info` VALUES (4, 2, '二级目录11', 2, 'http11', 'https11', 0, NULL, '2020-02-19 20:40:03', '2020-02-19 20:40:05');
INSERT INTO `video_info` VALUES (5, 2, '二级目录12', 2, 'http12', 'https12', 0, NULL, '2020-02-19 20:41:05', '2020-02-19 20:41:07');
INSERT INTO `video_info` VALUES (6, 3, '二级目录21', 2, 'http21', 'https21', 0, NULL, '2020-02-19 20:41:32', '2020-02-19 20:41:34');
INSERT INTO `video_info` VALUES (7, 3, '二级目录22', 2, 'http22', 'https22', 0, NULL, '2020-02-19 20:41:59', '2020-02-19 20:42:01');
INSERT INTO `video_info` VALUES (8, 3, '二级目录23', 2, 'http23', 'https23', 0, NULL, '2020-02-19 20:42:22', '2020-02-19 20:42:25');

SET FOREIGN_KEY_CHECKS = 1;
