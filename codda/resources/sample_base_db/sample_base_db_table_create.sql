SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `SB_DB` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_INFO_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '�Խûc /* comment truncated */ /* �ĺ���,
0 : ����, 1:����, 2:FAQ*/,
  `board_name` VARCHAR(30) NULL DEFAULT NULL COMMENT '�Խ��� �̸�',
  `board_info` TEXT NULL DEFAULT NULL COMMENT '�Խ��� ����',
  `admin_total` INT(11) NOT NULL DEFAULT 0 COMMENT '�����϶� �Խ��� �� ��ü ����, ���� : ����ڴ� ����(board_st:\'D\')�� ���(board_st:\'B\')���� �������� ������ ����(board_st:\'Y\')���� �Խ��Ǹ� ���� ���������� ������ ��� �Խ��ǿ� ���ؼ� ���� �����ϴ�.',
  `user_total` INT(11) NOT NULL DEFAULT 0 COMMENT '������϶� �Խ��� �� ��ü ����, ���� : ����ڴ� ����(board_st:\'D\')�� ���(board_st:\'B\')���� �������� ������ ����(board_st:\'Y\')���� �Խ��Ǹ� ���� ���������� ������ ��� �Խ��ǿ� ���ؼ� ���� �����ϴ�.',
  PRIMARY KEY (`board_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_MEMBER_TB` (
  `user_id` VARCHAR(20) NOT NULL COMMENT '����� ���̵�',
  `nickname` VARCHAR(45) NOT NULL COMMENT '����',
  `pwd_base64` VARCHAR(88) NULL DEFAULT NULL COMMENT '��й�ȣ, ��й�ȣ�� �ؽ� ������ ��ȯ�Ǿ� base64 ���·� ����ȴ�.',
  `pwd_salt_base64` VARCHAR(12) NULL DEFAULT NULL COMMENT '��й�ȣ�� �ؽ��� �ٲܶ� �� ���� ���ظ� �������� �Բ� ����ϴ� ���� ��',
  `member_type` CHAR NOT NULL COMMENT 'ȸ�� ����, A:������, M:�Ϲ�ȸ��',
  `member_st` CHAR NULL DEFAULT NULL COMMENT 'ȸ�� ����, Y : ����, B:���, W:Ż��',
  `pwd_hint` TINYTEXT NULL DEFAULT NULL COMMENT '��й�ȣ ��Ʈ, ��й�ȣ �нǽ� �亯 ������ ��������� �����ִ� ��Ʈ',
  `pwd_answer` TINYTEXT NULL DEFAULT NULL COMMENT '��й�ȣ �亯, ��й�ȣ �нǽ� ����ٸ� ��й�ȣ �� ���� Ȥ�� ��й�ȣ �ʱ�ȭ�� �����Ѵ�.',
  `pwd_fail_cnt` TINYINT(4) UNSIGNED NULL DEFAULT NULL COMMENT '��й�ȣ Ʋ�� Ƚ��, �α��ν� ��й�ȣ Ʋ�� ��� 1 �� �����ϸ� �ִ� n ������ �õ� �����ϴ�.  ��й�ȣ�� ������ ��� 0 ���� �ʱ�ȭ �ȴ�.',
  `reg_dt` DATETIME NULL DEFAULT NULL COMMENT 'ȸ�� ������',
  `mod_dt` DATETIME NULL DEFAULT NULL COMMENT 'ȸ�� ���� ������',
  `ip` VARCHAR(40) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `sb_member_idx1` (`nickname` ASC),
  INDEX `sb_member_idx2` (`member_st` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '�Խ��� ���� �ĺ���, � �Խ������� �����ϴ� �Խ��� ����(board_info) ���̺��� �ٶ󺻴�.',
  `board_no` INT(10) UNSIGNED NOT NULL COMMENT '�Խ��� ��ȣ,  1���� �����Ѵ�. 1 �� �ʱ�ȭ �Ǵ� ������ ���̺�(SB_SEQ_TB) �� ���� �Խ��� Ÿ�Ժ��� �Խ��� ��ȣ�� ����',
  `group_no` INT(10) UNSIGNED NOT NULL COMMENT '�׷� ��ȣ',
  `group_sq` SMALLINT(5) UNSIGNED NOT NULL COMMENT '�׷� �� ������ �׷� ��ȣ(=group_no)  ���� 0 ���� ���۵Ǵ� ����',
  `parent_no` INT(10) UNSIGNED NULL DEFAULT NULL COMMENT '�θ� �Խ��� ��ȣ,  �Խ��� ��ȣ�� 1���� �����ϸ� �θ� ���� ��� �θ� �Խ��� ��ȣ�� 0 ���� ���´�.',
  `depth` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT 'Ʈ�� ����,  0 ���� �����ϸ� Ʈ�� ���̰� 0 �� ��� �ֻ��� �۷ν� �ֻ��� ���� �������� ���� ����� �޸���. �ڽ� ���� ��� ���̴� �θ� ���� ��� ���̺��� 1 �� ũ��.',
  `view_cnt` INT(11) NULL DEFAULT NULL COMMENT '��ȸ��',
  `board_st` CHAR(1) NOT NULL COMMENT '�Խñ� ����, B : ���, D : ������ �Խñ�, Y : ���� �Խñ�',
  PRIMARY KEY (`board_id`, `board_no`),
  INDEX `sb_board_fk1_idx` (`board_id` ASC),
  UNIQUE INDEX `sb_board_idx1` (`board_id` ASC, `group_no` ASC, `group_sq` ASC),
  CONSTRAINT `sb_board_fk1`
    FOREIGN KEY (`board_id`)
    REFERENCES `SB_DB`.`SB_BOARD_INFO_TB` (`board_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_FILELIST_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL,
  `board_no` INT(10) UNSIGNED NOT NULL,
  `attached_file_sq` TINYINT(3) UNSIGNED NOT NULL COMMENT '÷�� ���� ����',
  `attached_fname` VARCHAR(255) NULL DEFAULT NULL COMMENT '÷�� ���� �̸�',
  PRIMARY KEY (`board_id`, `board_no`, `attached_file_sq`),
  CONSTRAINT `sb_board_filelist_fk1`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_VOTE_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL,
  `board_no` INT(10) UNSIGNED NOT NULL,
  `user_id` VARCHAR(20) NOT NULL,
  `ip` VARCHAR(40) NULL DEFAULT NULL,
  `reg_dt` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`board_no`, `user_id`, `board_id`),
  INDEX `sb_board_vote_fk2_idx` (`user_id` ASC),
  CONSTRAINT `sb_board_vote_fk2`
    FOREIGN KEY (`user_id`)
    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sb_board_vote_fk1`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_SEQ_TB` (
  `sq_id` TINYINT(3) UNSIGNED NOT NULL COMMENT '������ �ĺ���, 0:�޴�, 1:�����Խ��� ������, 2:�����Խ��ǽ�����, 3:FAQ������',
  `sq_value` INT(10) UNSIGNED NULL DEFAULT NULL COMMENT '������ ��, 1 ���� ����',
  `sq_name` VARCHAR(45) NULL DEFAULT NULL COMMENT '������ �̸�',
  PRIMARY KEY (`sq_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_GROUP_INFO_TB` (
  `group_id` TINYINT(4) NOT NULL COMMENT '�׷� �ĺ���',
  `group_name` VARCHAR(45) NULL DEFAULT NULL,
  `group_info` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`group_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_GROUP_TB` (
  `group_id` TINYINT(4) NOT NULL,
  `user_id` VARCHAR(20) NOT NULL,
  `reg_dt` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`group_id`, `user_id`),
  INDEX `sb_group_fk2_idx` (`user_id` ASC),
  CONSTRAINT `sb_group_fk1`
    FOREIGN KEY (`group_id`)
    REFERENCES `SB_DB`.`SB_GROUP_INFO_TB` (`group_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sb_group_fk2`
    FOREIGN KEY (`user_id`)
    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_SITEMENU_TB` (
  `menu_no` INT(10) UNSIGNED NOT NULL COMMENT '�޴� ��ȣ,  1���� ���۵ȴ�. 1 �� �ʱ�ȭ �Ǵ� ������ ���̺�(SB_SEQ_TB) �� ���� �޴� ��ȣ�� ����.',
  `parent_no` INT(10) UNSIGNED NOT NULL COMMENT '�θ� �޴� ��ȣ,  �޴� ��ȣ�� 1���� ���۵Ǹ� �θ� ���� ��� �θ� �޴� ��ȣ ����  0 ���� ���´�.',
  `depth` TINYINT(3) UNSIGNED NOT NULL COMMENT 'Ʈ�� ����,  0 ���� �����ϸ� �θ𺸴� + 1 �� ũ��',
  `order_sq` TINYINT(3) UNSIGNED NOT NULL COMMENT '���� ���̿����� �޴� ����',
  `menu_nm` VARCHAR(100) NOT NULL COMMENT '�޴� �̸�',
  `link_url` VARCHAR(2048) NOT NULL COMMENT '�޴��� �����Ǵ� ��ũ �ּ�',
  PRIMARY KEY (`menu_no`),
  INDEX `sb_sitemenu_idx1` (`order_sq` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_HISTORY_TB` (
  `board_id` TINYINT(3) UNSIGNED NOT NULL,
  `board_no` INT(10) UNSIGNED NOT NULL,
  `history_sq` TINYINT(3) UNSIGNED NOT NULL COMMENT '�����丮 ����',
  `subject` VARCHAR(255) NULL DEFAULT NULL,
  `content` TEXT NULL DEFAULT NULL,
  `modifier_id` VARCHAR(20) NOT NULL COMMENT '�ۼ���',
  `ip` VARCHAR(40) NULL DEFAULT NULL,
  `reg_dt` DATETIME NULL DEFAULT NULL COMMENT '���� �ۼ���',
  PRIMARY KEY (`board_id`, `board_no`, `history_sq`),
  INDEX `sb_board_history_fk2_idx` (`modifier_id` ASC),
  CONSTRAINT `sb_board_history_fk1`
    FOREIGN KEY (`board_id` , `board_no`)
    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_id` , `board_no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `sb_board_history_fk2`
    FOREIGN KEY (`modifier_id`)
    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
