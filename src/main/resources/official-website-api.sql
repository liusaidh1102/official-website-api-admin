create table china_zipcode
(
    merger_name varchar(255) null comment '聚合全称',
    zipcode     int          null comment '邮政编码'
);

create table community_tag
(
    tid      bigint auto_increment comment '标签ID'
        primary key,
    tag_name varchar(20)   not null comment '标签内容',
    tag_uses int default 0 null comment '标签使用量'
)
    comment '标签表' row_format = DYNAMIC;

create table community_tag_post_relations
(
    relation_id bigint auto_increment comment '关联id'
        primary key,
    tag_id      bigint  not null comment '标签id',
    post_id     bigint  not null comment '帖子id',
    type        tinyint null comment '文章类型',
    constraint tag_id
        unique (tag_id, post_id)
)
    comment '帖子标签关联表';

create table files
(
    id          int auto_increment comment 'id'
        primary key,
    upload_id   varchar(255)         null comment '分片上传uploadId',
    file_md5    varchar(255)         null comment '文件md5',
    url         varchar(500)         null comment '下载链接',
    file_name   varchar(255)         null comment '文件名称',
    bucket_name varchar(255)         null comment '桶名',
    file_type   varchar(255)         null comment '文件类型',
    file_size   bigint               null comment '文件大小(byte)',
    chunk_size  bigint               null comment '每个分片的大小（byte）',
    chunk_num   int                  null comment '分片数量',
    is_delete   tinyint(1) default 0 null comment '是否删除',
    enable      tinyint(1) default 1 null comment '是否禁用链接',
    create_time datetime             null comment '创建时间',
    update_time datetime             null comment '更新时间',
    constraint idx_uinque_md5
        unique (file_md5)
)
    collate = utf8mb4_unicode_ci
    row_format = DYNAMIC;

create table message
(
    message_id   bigint auto_increment comment '消息ID'
        primary key,
    sender_id    bigint                             not null comment '发送者用户ID',
    receiver_id  bigint                             not null comment '接收者用户ID',
    post_id      bigint                             not null comment '文章ID',
    message_type int                                not null comment '消息类型ID',
    content      mediumtext                         null comment '消息内容',
    status       tinyint  default 0                 null comment '消息状态（已读1、未读0）',
    created_at   datetime default CURRENT_TIMESTAMP not null comment '消息发送时间'
)
    comment '消息表';

create table message_type
(
    type_id    bigint auto_increment comment '消息类型ID'
        primary key,
    type_name  varchar(255)                       not null comment '消息类型名称（点赞1、收藏2，评论文章3、评论评论4、系统通知5等）',
    created_at datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '消息类型表';

create table message_user_notice
(
    id        bigint auto_increment comment '主键自增'
        primary key,
    user_id   bigint            null comment '用户id',
    notice_id bigint            null comment '公告id',
    status    tinyint default 0 null comment '消息状态（已读1、未读0）'
)
    comment '公告-用户表';

create table post
(
    id            bigint auto_increment comment '主键自增'
        primary key,
    user_id       bigint            null comment '发帖用户id',
    view_count    int     default 0 null comment '访问量',
    like_count    int     default 0 null,
    post_time     datetime          null comment '发布时间,格式为YYYY-MM-DD HH:MM:SS',
    title         varchar(30)       null comment '贴子标题(字符 <= 30)',
    post_txt      mediumtext        null comment '贴子文本内容(文本 <= 16Mb)',
    post_abstract varchar(100)      null comment '贴子摘要(字符 <= 100)',
    type          tinyint           null comment '类型(1博客|2公告|3交流 |4头脑风暴)',
    delete_flag   tinyint default 0 null comment '是否删除,0:未删除,1:已删除'
)
    comment '用户发的贴子内容';

create table post_comment_all
(
    id           bigint auto_increment comment '主键自增'
        primary key,
    comment_id   bigint            null comment '一级评论id',
    comment_user bigint            null comment '评论者(用户)id',
    point_user   bigint            null comment '该评论指向的用户id',
    comment_txt  mediumtext        null comment '评论内容',
    comment_time datetime          null comment '评论时间,格式为YYYY-MM-DD HH:MM:SS',
    like_count   bigint  default 0 null comment '点赞数量',
    delete_flag  tinyint default 0 null comment '是否删除,0:未删除,1:已删除'
)
    comment '无极评论';

create table post_comment_one
(
    id           bigint auto_increment comment '主键自增'
        primary key,
    post_id      bigint            null comment '被评论的贴子id',
    comment_user bigint            null comment '评论者(用户)id',
    comment_txt  mediumtext        null comment '评论内容',
    comment_time datetime          null comment '评论时间,格式为YYYY-MM-DD HH:MM:SS',
    like_count   bigint  default 0 null comment '点赞的数量',
    delete_flag  tinyint default 0 null comment '是否删除,0:未删除,1:已删除'
)
    comment '帖子一级评论';

create index idx_post_comment_one_post_id
    on post_comment_one (post_id);

create table recruit_class
(
    id          bigint auto_increment comment 'ID，主键自增'
        primary key,
    grade       varchar(10)                          null comment '年级',
    clazz       varchar(15)                          null comment '班级',
    create_time datetime   default CURRENT_TIMESTAMP null comment '添加的时间',
    is_deleted  tinyint(1) default 0                 null comment '是否删除，0表示未删除，1表示已删除'
)
    comment '面试支持的班级' collate = utf8mb4_unicode_ci;

create table recruit_grade
(
    id          int auto_increment comment 'id
'
        primary key,
    grade       varchar(255)                        not null comment '年级',
    create_time timestamp default CURRENT_TIMESTAMP null comment '时间，按照时间进行排序'
);

create table recruit_interview
(
    id               bigint auto_increment comment 'ID，主键自增'
        primary key,
    user_id          bigint            null comment '面试人的ID，与面试人表关联',
    first_hr         bigint            null comment '面试官1,和面试官的id关联',
    second_hr        bigint            null comment '面试官2，和面试官的id关联',
    third_hr         bigint            null comment '面试官3，和面试官的id关联',
    place            varchar(30)       null comment '面试的地点',
    comment          varchar(500)      null comment '500字以内的面评(一面)',
    interview_status tinyint default 1 null comment '0代表待安排；1代表待面试；2代表已录取；3代表未录；4代表已淘汰待二面',
    round            tinyint default 1 null comment '面试的轮次：1代表一面；2代表二面（默认值是1）',
    name             varchar(255)      null comment '面试人的姓名',
    grade            varchar(255)      null comment '年级（根据年级筛选的时候比较的是字符串）',
    start_time       datetime          null comment '面试开始时间',
    end_time         datetime          null comment '面试结束时间'
)
    comment '面试评价表' collate = utf8mb4_unicode_ci;

create table recruit_user
(
    id          bigint auto_increment comment '报名用户id，主键自增'
        primary key,
    name        varchar(50)                                null comment '姓名',
    grade       varchar(50)      default '2025级'          null comment '年级',
    clazz       varchar(15)                                null comment '班级',
    student_id  varchar(50)                                null comment '学号',
    sex         varchar(10)                                null comment '性别',
    qq_number   varchar(50)                                null comment 'QQ',
    email       varchar(50)                                null comment '邮箱',
    file_url    varchar(255)                               null comment '报名的简历pdf',
    create_time datetime         default CURRENT_TIMESTAMP null comment '投递简历的时间',
    update_time datetime         default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '上次状态修改的时间',
    is_deleted  tinyint unsigned default '0'               null comment '是否删除，0表示未删除，1表示已删除',
    status      tinyint          default 0                 null comment '报名用户的状态，0代表待安排；1代表待面试；2代表已录取；3代表未录取'
)
    comment '报名的用户' collate = utf8mb4_unicode_ci;

create table user
(
    id                     bigint auto_increment comment '主键自增'
        primary key,
    ding_user_id           varchar(200)                       null comment '对应钉钉中的用户id',
    username               varchar(15)                        null comment '学号[账号]',
    email                  varchar(25)                        null comment '邮箱(<=25)',
    password               varchar(80)                        null comment '密码',
    name                   varchar(18)                        null comment '姓名 <= 5',
    sex                    varchar(2)                         null comment '性别',
    clazz                  varchar(15)                        null comment '班级<= 10',
    grade                  varchar(10)                        null comment '年级',
    `group`                varchar(5)                         null comment '组别',
    direction              varchar(10) default '暂未分端'     null comment '方向<= 10',
    study_id               varchar(15)                        null comment '学号<= 15',
    head_portrait          varchar(150)                       null comment '头像',
    phone                  varchar(11)                        null comment '手机号',
    qq                     varchar(15)                        null comment 'qq号',
    graduation_destination varchar(20) default '暂无'         null comment '毕业去向',
    user_destination       varchar(50) default '暂无个性签名' null comment '个性签名',
    life_photo             varchar(5000)                      null comment '生活照(文本为json格式的集合)',
    last_login_time        datetime                           null comment '最后登录时间,格式为YYYY-MM-DD HH:MM:SS',
    current_login_time     datetime                           null comment '当前登录时间',
    is_interview           tinyint     default 0              null comment '是否为面试官 1 为 是 | 0 为不是',
    lan_qiao_count         tinyint     default 0              null comment '蓝桥杯获奖数',
    copyright_count        tinyint     default 0              null comment '软件著作数',
    salary_year            int         default 0              null comment '年薪',
    area                   varchar(50)                        null comment '地区',
    delete_flag            tinyint     default 0              null comment '是否删除,0:未删除,1:已删除',
    constraint ding_user_id
        unique (ding_user_id),
    constraint email
        unique (email),
    constraint username
        unique (username)
)
    comment '用户表';

create table user_collect
(
    id           bigint auto_increment comment '主键自增'
        primary key,
    user_id      bigint            null comment '用户id',
    collect_post bigint            null comment '收藏的贴子id',
    collect_time datetime          null comment '收藏时间,格式为YYYY-MM-DD HH:MM:SS',
    delete_flag  tinyint default 0 null comment '是否删除,0:未删除,1:已删除'
)
    comment '用户的收藏[贴子]';

create table user_course
(
    id          bigint auto_increment comment '主键自增'
        primary key,
    use_id      bigint            null comment '用户id',
    monday      varchar(1000)     null comment '星期一的课表',
    tuesday     varchar(1000)     null comment '星期二的课表',
    wednesday   varchar(1000)     null comment '星期三的课表',
    thursday    varchar(1000)     null comment '星期四的课表',
    friday      varchar(1000)     null comment '星期五的课表',
    saturday    varchar(1000)     null comment '星期六的课表',
    sunday      varchar(1000)     null comment '星期日的课表',
    course_html mediumtext        null comment '用于存储教务处课表的html(列表模式)',
    start_time  date              null comment '第一周的星期一的日期（用于计算本学期已过的周数）',
    delete_flag tinyint default 0 null comment '是否删除，0 未删除 1 已删除',
    constraint uk_user_course_use_id
        unique (use_id)
)
    comment '用户的课程表';

create table user_group_leader
(
    id              bigint auto_increment comment '主键自增'
        primary key,
    group_leader_id bigint      null comment '组长id',
    group_postiton  varchar(15) null comment '组的位置(那一组)'
);

create table user_permission
(
    id        bigint auto_increment comment '主键自增'
        primary key,
    user_id   bigint      null comment '用户id',
    authority varchar(16) null comment 'community:社区 |admin:概况+通讯录 |community_admin:社区管理 |recruit:招新管理 |admin_plus:权限管理'
)
    comment '角色表';

create definer = root@`%` view v_summarize as
select (select count(0)
        from `wlgzs_official_website_api`.`user`
        where (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))   AS `allTeamUserCount`,
       (select avg(`wlgzs_official_website_api`.`user`.`salary_year`)
        from `wlgzs_official_website_api`.`user`
        where (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))   AS `salaryYearAll`,
       (select sum(`wlgzs_official_website_api`.`user`.`copyright_count`)
        from `wlgzs_official_website_api`.`user`
        where (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))   AS `copyrightCount`,
       (select sum(`wlgzs_official_website_api`.`user`.`lan_qiao_count`)
        from `wlgzs_official_website_api`.`user`
        where (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))   AS `lanQiaoCount`,
       (select count(0)
        from `wlgzs_official_website_api`.`user`
        where ((`wlgzs_official_website_api`.`user`.`direction` = '后端') and
               (`wlgzs_official_website_api`.`user`.`grade` <= (year(now()) - 1)) and
               (`wlgzs_official_website_api`.`user`.`grade` >= (year(now()) - 3)) and
               (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))) AS `javaCurrent`,
       (select count(0)
        from `wlgzs_official_website_api`.`user`
        where ((`wlgzs_official_website_api`.`user`.`direction` = '前端') and
               (`wlgzs_official_website_api`.`user`.`grade` <= (year(now()) - 1)) and
               (`wlgzs_official_website_api`.`user`.`grade` >= (year(now()) - 3)) and
               (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))) AS `htmlCurrent`,
       (select count(0)
        from `wlgzs_official_website_api`.`user`
        where ((`wlgzs_official_website_api`.`user`.`direction` = '考研') and
               (`wlgzs_official_website_api`.`user`.`grade` <= (year(now()) - 1)) and
               (`wlgzs_official_website_api`.`user`.`grade` >= (year(now()) - 3)) and
               (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))) AS `examCurrent`,
       (select count(0)
        from `wlgzs_official_website_api`.`user`
        where ((`wlgzs_official_website_api`.`user`.`direction` = '实习') and
               (`wlgzs_official_website_api`.`user`.`grade` <= (year(now()) - 1)) and
               (`wlgzs_official_website_api`.`user`.`grade` >= (year(now()) - 3)) and
               (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))) AS `workCurrent`,
       (select count(0)
        from `wlgzs_official_website_api`.`user`
        where (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))   AS `userCount`,
       (select count(0)
        from `wlgzs_official_website_api`.`post`
        where ((`wlgzs_official_website_api`.`post`.`type` = 1) and
               (`wlgzs_official_website_api`.`post`.`delete_flag` = 0))) AS `postB`,
       (select count(0)
        from `wlgzs_official_website_api`.`post`
        where ((`wlgzs_official_website_api`.`post`.`type` = 2) and
               (`wlgzs_official_website_api`.`post`.`delete_flag` = 0))) AS `postG`,
       (select count(0)
        from `wlgzs_official_website_api`.`post`
        where ((`wlgzs_official_website_api`.`post`.`type` = 3) and
               (`wlgzs_official_website_api`.`post`.`delete_flag` = 0))) AS `postJ`,
       (select count(0)
        from `wlgzs_official_website_api`.`post`
        where ((`wlgzs_official_website_api`.`post`.`type` = 4) and
               (`wlgzs_official_website_api`.`post`.`delete_flag` = 0))) AS `postT`,
       (select count(0)
        from `wlgzs_official_website_api`.`user`
        where ((`wlgzs_official_website_api`.`user`.`direction` = '后端') and
               (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))) AS `javaAll`,
       (select count(0)
        from `wlgzs_official_website_api`.`user`
        where ((`wlgzs_official_website_api`.`user`.`direction` = '前端') and
               (`wlgzs_official_website_api`.`user`.`delete_flag` = 0))) AS `htmlAll`;

create definer = root@`%` view v_summarize_zipcode as
select `wlgzs_official_website_api`.`china_zipcode`.`zipcode` AS `zipcode`, `info`.`people_count` AS `peopleCount`
from ((with `parsed_address` as (select substring_index(`wlgzs_official_website_api`.`user`.`area`, ',', 2) AS `city`,
                                        if(((length(`wlgzs_official_website_api`.`user`.`area`) -
                                             length(replace(`wlgzs_official_website_api`.`user`.`area`, ',', ''))) >=
                                            2), substring_index(`wlgzs_official_website_api`.`user`.`area`, ',', 3),
                                           NULL)                                                            AS `province`,
                                        if(((length(`wlgzs_official_website_api`.`user`.`area`) -
                                             length(replace(`wlgzs_official_website_api`.`user`.`area`, ',', ''))) >=
                                            3), substring_index(`wlgzs_official_website_api`.`user`.`area`, ',', 4),
                                           NULL)                                                            AS `district`
                                 from `wlgzs_official_website_api`.`user`
                                 where (`wlgzs_official_website_api`.`user`.`area` is not null))
       select `parsed_address`.`city` AS `city`, count(0) AS `people_count`
       from `parsed_address`
       where (`parsed_address`.`city` is not null)
       group by `parsed_address`.`city`
       union
       select `parsed_address`.`province` AS `province`, count(0) AS `count(*)`
       from `parsed_address`
       where (`parsed_address`.`province` is not null)
       group by `parsed_address`.`province`
       union
       select `parsed_address`.`district` AS `district`, count(0) AS `count(*)`
       from `parsed_address`
       where (`parsed_address`.`district` is not null)
       group by `parsed_address`.`district`) `info` left join `wlgzs_official_website_api`.`china_zipcode`
      on ((`wlgzs_official_website_api`.`china_zipcode`.`merger_name` = `info`.`city`)));

-- comment on column v_summarize_zipcode.zipcode not supported: 邮政编码

create definer = root@`%` view v_user_grade_per as
select distinct `wlgzs_official_website_api`.`user`.`grade` AS `grade`,
                `java`.`salaryYearPerJava`                  AS `salaryYearPerJava`,
                `java`.`PeopleCountPerJava`                 AS `PeopleCountPerJava`,
                `html`.`salaryYearPerHtml`                  AS `salaryYearPerHtml`,
                `html`.`PeopleCountPerHtml`                 AS `PeopleCountPerHtml`
from ((`wlgzs_official_website_api`.`user` left join (select `wlgzs_official_website_api`.`user`.`grade`            AS `grade`,
                                                             avg(`wlgzs_official_website_api`.`user`.`salary_year`) AS `salaryYearPerHtml`,
                                                             count(0)                                               AS `PeopleCountPerHtml`
                                                      from `wlgzs_official_website_api`.`user`
                                                      where ((`wlgzs_official_website_api`.`user`.`delete_flag` = 0) and
                                                             (`wlgzs_official_website_api`.`user`.`direction` = '前端'))
                                                      group by `wlgzs_official_website_api`.`user`.`grade`) `html`
       on ((`wlgzs_official_website_api`.`user`.`grade` = `html`.`grade`))) left join (select `wlgzs_official_website_api`.`user`.`grade`            AS `grade`,
                                                                                              avg(`wlgzs_official_website_api`.`user`.`salary_year`) AS `salaryYearPerJava`,
                                                                                              count(0)                                               AS `PeopleCountPerJava`
                                                                                       from `wlgzs_official_website_api`.`user`
                                                                                       where ((`wlgzs_official_website_api`.`user`.`delete_flag` = 0) and
                                                                                              (`wlgzs_official_website_api`.`user`.`direction` = '后端'))
                                                                                       group by `wlgzs_official_website_api`.`user`.`grade`) `java`
      on ((`wlgzs_official_website_api`.`user`.`grade` = `java`.`grade`)))
where (`wlgzs_official_website_api`.`user`.`delete_flag` = 0);

-- comment on column v_user_grade_per.grade not supported: 年级

create definer = root@`%` view v_user_permission as
select `wlgzs_official_website_api`.`user`.`id`                                               AS `id`,
       `wlgzs_official_website_api`.`user`.`username`                                         AS `username`,
       `wlgzs_official_website_api`.`user`.`name`                                             AS `name`,
       `wlgzs_official_website_api`.`user`.`sex`                                              AS `sex`,
       group_concat(`wlgzs_official_website_api`.`user_permission`.`authority` separator ',') AS `authority`
from (`wlgzs_official_website_api`.`user` left join `wlgzs_official_website_api`.`user_permission`
      on ((`wlgzs_official_website_api`.`user`.`id` = `wlgzs_official_website_api`.`user_permission`.`user_id`)))
where (`wlgzs_official_website_api`.`user`.`delete_flag` = 0)
group by `wlgzs_official_website_api`.`user`.`id`;

-- comment on column v_user_permission.id not supported: 主键自增

-- comment on column v_user_permission.username not supported: 学号[账号]

-- comment on column v_user_permission.name not supported: 姓名 <= 5

-- comment on column v_user_permission.sex not supported: 性别

create definer = root@`%` view v_user_post as
select `wlgzs_official_website_api`.`post`.`user_id`                                  AS `userId`,
       `wlgzs_official_website_api`.`post`.`id`                                       AS `postId`,
       `wlgzs_official_website_api`.`post`.`view_count`                               AS `viewCount`,
       `wlgzs_official_website_api`.`post`.`post_time`                                AS `putTime`,
       `wlgzs_official_website_api`.`post`.`title`                                    AS `title`,
       `wlgzs_official_website_api`.`post`.`post_abstract`                            AS `postAbstract`,
       `wlgzs_official_website_api`.`post`.`like_count`                               AS `postLikeCount`,
       ifnull((select (`t2`.`oneCount` + `t2`.`towCount`)
               from (select `wlgzs_official_website_api`.`post_comment_one`.`post_id`                                 AS `post_id`,
                            count(0)                                                                                  AS `oneCount`,
                            (select sum(`t1`.`towCount`) AS `tow`
                             from (select (select count(0)
                                           from `wlgzs_official_website_api`.`post_comment_all`
                                           where ((`wlgzs_official_website_api`.`post_comment_all`.`comment_id` =
                                                   `wlgzs_official_website_api`.`post_comment_one`.`id`) and
                                                  (`wlgzs_official_website_api`.`post_comment_all`.`delete_flag` = 0))) AS `towCount`
                                   from `wlgzs_official_website_api`.`post_comment_one`
                                   where ((`wlgzs_official_website_api`.`post_comment_one`.`post_id` =
                                           `wlgzs_official_website_api`.`post`.`id`) and
                                          (`wlgzs_official_website_api`.`post_comment_one`.`delete_flag` = 0))) `t1`) AS `towCount`
                     from `wlgzs_official_website_api`.`post_comment_one`
                     where ((`wlgzs_official_website_api`.`post_comment_one`.`post_id` =
                             `wlgzs_official_website_api`.`post`.`id`) and
                            (`wlgzs_official_website_api`.`post_comment_one`.`delete_flag` = 0))) `t2`
               where (`t2`.`post_id` = `wlgzs_official_website_api`.`post`.`id`)), 0) AS `commentCount`,
       (select count(0)
        from `wlgzs_official_website_api`.`user_collect`
        where ((`wlgzs_official_website_api`.`user_collect`.`collect_post` =
                `wlgzs_official_website_api`.`post`.`id`) and
               (`wlgzs_official_website_api`.`user_collect`.`delete_flag` = 0)))      AS `collectCount`
from `wlgzs_official_website_api`.`post`
where (`wlgzs_official_website_api`.`post`.`delete_flag` = 0);

-- comment on column v_user_post.userId not supported: 发帖用户id

-- comment on column v_user_post.postId not supported: 主键自增

-- comment on column v_user_post.viewCount not supported: 访问量

-- comment on column v_user_post.putTime not supported: 发布时间,格式为YYYY-MM-DD HH:MM:SS

-- comment on column v_user_post.title not supported: 贴子标题(字符 <= 30)

-- comment on column v_user_post.postAbstract not supported: 贴子摘要(字符 <= 100)

create definer = root@`%` view v_user_post_all_info as
select `wlgzs_official_website_api`.`user`.`id`                                                                                                     AS `userId`,
       (select ifnull(sum(`wlgzs_official_website_api`.`post`.`like_count`), 0) AS `allLikeCount`
        from `wlgzs_official_website_api`.`post`
        where ((`wlgzs_official_website_api`.`post`.`user_id` = `userId`) and
               (`wlgzs_official_website_api`.`post`.`type` = 1) and
               (`wlgzs_official_website_api`.`post`.`delete_flag` = 0)))                                                                            AS `allLikeCount`,
       (select count(0)
        from `wlgzs_official_website_api`.`post`
        where ((`wlgzs_official_website_api`.`post`.`user_id` = `userId`) and
               (`wlgzs_official_website_api`.`post`.`delete_flag` = 0)))                                                                            AS `allPostCount`,
       (select count(0)
        from (`wlgzs_official_website_api`.`user_collect` left join `wlgzs_official_website_api`.`post`
              on (((`wlgzs_official_website_api`.`post`.`id` =
                    `wlgzs_official_website_api`.`user_collect`.`collect_post`) and
                   (`wlgzs_official_website_api`.`user_collect`.`delete_flag` = 0))))
        where ((`wlgzs_official_website_api`.`post`.`user_id` = `userId`) and
               (`wlgzs_official_website_api`.`post`.`delete_flag` = 0) and
               (`wlgzs_official_website_api`.`post`.`type` = 1)))                                                                                   AS `allCollectCount`,
       ((select count(0)
         from (`wlgzs_official_website_api`.`post_comment_one` left join `wlgzs_official_website_api`.`post`
               on (((`wlgzs_official_website_api`.`post`.`id` =
                     `wlgzs_official_website_api`.`post_comment_one`.`post_id`) and
                    (`wlgzs_official_website_api`.`post_comment_one`.`delete_flag` = 0))))
         where ((`wlgzs_official_website_api`.`post`.`user_id` = `userId`) and
                (`wlgzs_official_website_api`.`post`.`delete_flag` = 0) and
                (`wlgzs_official_website_api`.`post`.`type` = 1))) + (select count(0)
                                                                      from `wlgzs_official_website_api`.`post_comment_all`
                                                                      where (`wlgzs_official_website_api`.`post_comment_all`.`comment_id` in
                                                                             (select `wlgzs_official_website_api`.`post_comment_one`.`id`
                                                                              from (`wlgzs_official_website_api`.`post_comment_one` left join `wlgzs_official_website_api`.`post`
                                                                                    on ((`wlgzs_official_website_api`.`post`.`id` =
                                                                                         `wlgzs_official_website_api`.`post_comment_one`.`post_id`)))
                                                                              where ((`wlgzs_official_website_api`.`post`.`user_id` = `userId`) and
                                                                                     (`wlgzs_official_website_api`.`post`.`type` = 1))) and
                                                                             (`wlgzs_official_website_api`.`post_comment_all`.`delete_flag` = 0)))) AS `allCommentCount`
from `wlgzs_official_website_api`.`user`;

-- comment on column v_user_post_all_info.userId not supported: 主键自增

create definer = root@`%` view v_user_post_collect as
select `wlgzs_official_website_api`.`user_collect`.`user_id`                          AS `userId`,
       `wlgzs_official_website_api`.`post`.`id`                                       AS `postId`,
       `wlgzs_official_website_api`.`user_collect`.`id`                               AS `collectId`,
       `wlgzs_official_website_api`.`post`.`view_count`                               AS `viewCount`,
       `wlgzs_official_website_api`.`post`.`post_time`                                AS `putTime`,
       `wlgzs_official_website_api`.`post`.`title`                                    AS `title`,
       `wlgzs_official_website_api`.`post`.`post_abstract`                            AS `postAbstract`,
       `wlgzs_official_website_api`.`post`.`like_count`                               AS `postLikeCount`,
       ifnull((select (`t2`.`oneCount` + `t2`.`towCount`)
               from (select `wlgzs_official_website_api`.`post_comment_one`.`post_id`                                 AS `post_id`,
                            count(0)                                                                                  AS `oneCount`,
                            (select sum(`t1`.`towCount`) AS `tow`
                             from (select (select count(0)
                                           from `wlgzs_official_website_api`.`post_comment_all`
                                           where ((`wlgzs_official_website_api`.`post_comment_all`.`comment_id` =
                                                   `wlgzs_official_website_api`.`post_comment_one`.`id`) and
                                                  (`wlgzs_official_website_api`.`post_comment_all`.`delete_flag` = 0))) AS `towCount`
                                   from `wlgzs_official_website_api`.`post_comment_one`
                                   where ((`wlgzs_official_website_api`.`post_comment_one`.`post_id` =
                                           `wlgzs_official_website_api`.`post`.`id`) and
                                          (`wlgzs_official_website_api`.`post_comment_one`.`delete_flag` = 0))) `t1`) AS `towCount`
                     from `wlgzs_official_website_api`.`post_comment_one`
                     where ((`wlgzs_official_website_api`.`post_comment_one`.`post_id` =
                             `wlgzs_official_website_api`.`post`.`id`) and
                            (`wlgzs_official_website_api`.`post_comment_one`.`delete_flag` = 0))) `t2`
               where (`t2`.`post_id` = `wlgzs_official_website_api`.`post`.`id`)), 0) AS `commentCount`,
       (select count(0)
        from `wlgzs_official_website_api`.`user_collect`
        where ((`wlgzs_official_website_api`.`user_collect`.`collect_post` =
                `wlgzs_official_website_api`.`post`.`id`) and
               (`wlgzs_official_website_api`.`user_collect`.`delete_flag` = 0)))      AS `collectCount`
from (`wlgzs_official_website_api`.`user_collect` left join `wlgzs_official_website_api`.`post`
      on ((`wlgzs_official_website_api`.`user_collect`.`collect_post` = `wlgzs_official_website_api`.`post`.`id`)))
where (`wlgzs_official_website_api`.`user_collect`.`delete_flag` = 0);

-- comment on column v_user_post_collect.userId not supported: 用户id

-- comment on column v_user_post_collect.postId not supported: 主键自增

-- comment on column v_user_post_collect.collectId not supported: 主键自增

-- comment on column v_user_post_collect.viewCount not supported: 访问量

-- comment on column v_user_post_collect.putTime not supported: 发布时间,格式为YYYY-MM-DD HH:MM:SS

-- comment on column v_user_post_collect.title not supported: 贴子标题(字符 <= 30)

-- comment on column v_user_post_collect.postAbstract not supported: 贴子摘要(字符 <= 100)

create definer = root@`%` view v_user_search_like as
select `wlgzs_official_website_api`.`user`.`id`                                     AS `userId`,
       `wlgzs_official_website_api`.`user`.`name`                                   AS `name`,
       `wlgzs_official_website_api`.`user`.`head_portrait`                          AS `headPortrait`,
       `wlgzs_official_website_api`.`user`.`user_destination`                       AS `userDestination`,
       (select count(0)
        from `wlgzs_official_website_api`.`post`
        where ((`wlgzs_official_website_api`.`post`.`user_id` = `wlgzs_official_website_api`.`user`.`id`) and
               (`wlgzs_official_website_api`.`post`.`delete_flag` = 0)))            AS `postCount`,
       ifnull((select sum(`wlgzs_official_website_api`.`post`.`view_count`)
               from `wlgzs_official_website_api`.`post`
               where ((`wlgzs_official_website_api`.`post`.`user_id` = `wlgzs_official_website_api`.`user`.`id`) and
                      (`wlgzs_official_website_api`.`post`.`delete_flag` = 0))), 0) AS `viewCount`
from `wlgzs_official_website_api`.`user`
where (`wlgzs_official_website_api`.`user`.`delete_flag` = 0);

-- comment on column v_user_search_like.userId not supported: 主键自增

-- comment on column v_user_search_like.name not supported: 姓名 <= 5

-- comment on column v_user_search_like.headPortrait not supported: 头像

-- comment on column v_user_search_like.userDestination not supported: 个性签名

create definer = root@`%` view v_user_team_able as
select `temp`.`grade` AS `grade`, group_concat(`temp`.`group` separator ',') AS `group`
from (select distinct `wlgzs_official_website_api`.`user`.`grade`                                                                                                                                  AS `grade`,
                      concat(`wlgzs_official_website_api`.`user`.`group`, '$', count(0)
                                                                                     OVER (PARTITION BY `wlgzs_official_website_api`.`user`.`grade`,`wlgzs_official_website_api`.`user`.`group` )) AS `group`
      from `wlgzs_official_website_api`.`user`
      where ((`wlgzs_official_website_api`.`user`.`delete_flag` = 0) and
             (`wlgzs_official_website_api`.`user`.`group` is not null))
      order by `wlgzs_official_website_api`.`user`.`grade` desc, `group`) `temp`
group by `temp`.`grade`;

-- comment on column v_user_team_able.grade not supported: 年级

