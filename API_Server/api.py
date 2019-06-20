# -*- coding: utf-8 -*-
#!/usr/bin/env python3

from flask import Blueprint, request
from datetime import date, datetime
import MySQLdb
import json

###
# MySQL DB 접속 설정
###
mysql_host = "localhost"
mysql_userid = "DB_ID"
mysql_password = "DB_PASSWORD"
mysql_dbname = "DB_NAME"

IMG_URL = "BLANK URL"

def set_database_for_utf8(cursor):
    cursor.execute("SET NAMES utf8;")
    cursor.execute("SET CHARACTER SET utf8;")
    cursor.execute("SET character_set_connection=utf8;")

    return

def connect_mysql_database():
    db = MySQLdb.connect(
            mysql_host,
            mysql_userid,
            mysql_password,
            mysql_dbname)
    db.set_character_set("utf8")

    return db

def disconnect_mysql_database(db):
    db.close()
    return

api_bp = Blueprint("api", __name__)

@api_bp.route( "/user", methods=["POST"])
def create_user():
    json_data = request.get_json(force=True)
    #print(json_data)

    user_id = json_data["id"] # 16자리 FB Access token
    user_name = json_data["name"] # 실명

    if len(user_id) < 16 or len(user_id) > 21:
        return json.dumps({"result": "error", "msg": "id(accres token)'s length is weird"})

    search_id_sql = "SELECT * FROM `users` WHERE `id` LIKE '" + user_id + "'"
    #print(search_id_sql)

    db = connect_mysql_database()
    cursor = db.cursor()
    set_database_for_utf8(cursor)
    cursor.execute(search_id_sql)
    rows = cursor.fetchall()

    if rows:
        return json.dumps({"result": "error", "msg": "ID exists. please change id to something else"})


    insert_sql = "INSERT INTO `users` (`id`,`full_name`, `created_at`) \
            VALUES('{}', '{}', CURRENT_TIMESTAMP)".format(user_id, user_name)
    #print(insert_sql)

    cursor.execute(insert_sql)
    db.commit()

    disconnect_mysql_database(db)

    return json.dumps({'result': 'ok'})

@api_bp.route("/user", methods=["GET"])
def user_login():
    user_id = request.form["id"]
    #print(user_id)

    if len(user_id) < 16 or len(user_id) > 21:
        return json.dumps({"result": "error", "msg": "id(accres token)'s length is weird"})

    search_id_sql = "SELECT * FROM `users` WHERE `id` LIKE '" + user_id + "'"
    #print(search_id_sql)

    db = connect_mysql_database()
    cursor = db.cursor()
    set_database_for_utf8(cursor)
    cursor.execute(search_id_sql)
    rows = cursor.fetchall()

    disconnect_mysql_database(db)

    if rows:
        return json.dumps({"result": "ok", "user_info": {"name": rows[0][1]}})

    return json.dumps({"result": "error", "msg": "no account was found with this id(access token)"})


@api_bp.route("center", methods=["POST"])
def center_create():
    json_data = request.get_json(force=True)

    owner_id = json_data["owner"]
    center_name = json_data["name"]
    latitude = json_data["latitude"]
    longitude = json_data["longitude"]

    if len(owner_id) < 16 or len(owner_id) > 21:
        return json.dumps({"result": "error", "msg": "id(accres token)'s length is weird"})

    search_id_sql = "SELECT * FROM `users` WHERE `id` LIKE '" + owner_id + "'"
    #print(search_id_sql)

    db = connect_mysql_database()
    cursor = db.cursor()
    set_database_for_utf8(cursor)
    cursor.execute(search_id_sql)
    rows = cursor.fetchall()

    if not rows:
        return json.dumps({"result": "error", "msg": "no account was found with this id(access token)"})

    search_res_sql = "SELECT * FROM `food_center` WHERE `owner_id` LIKE '" + owner_id + "' AND `name` LIKE '" + center_name + "'"
    #print(search_res_sql)

    cursor.execute(search_res_sql)
    rows = cursor.fetchall()

    if rows:
        return json.dumps({"result": "error", "msg": "Duplicated data found"})

    insert_sql = "INSERT INTO `food_center` (`name`,`latitude`, `longitude`, `owner_id`, `created_at`) \
            VALUES('{}', '{}', '{}', '{}', CURRENT_TIMESTAMP)".format(center_name, latitude, longitude, owner_id)
    #print(insert_sql)

    cursor.execute(insert_sql)
    db.commit()

    search_fc_with_id_sql = "SELECT `id` FROM `food_center` WHERE `owner_id` LIKE '" + owner_id + "' AND `name` LIKE '" + center_name + "'"
    #print(search_res_sql)

    cursor.execute(search_fc_with_id_sql)
    res_id = cursor.fetchall()
    center_id = res_id[0][0]

    center_image_update_sql = "UPDATE `food_center` SET `image_path`='http://" + IMG_URL + "/upload/center_img_" + str(center_id) + ".jpg' WHERE `id`=" + str(center_id)
    print(center_image_update_sql)

    cursor.execute(center_image_update_sql)
    db.commit()

    disconnect_mysql_database(db)

    return json.dumps({"result": "ok", "center_id": center_id})

@api_bp.route("/center_info", methods=["GET"])
def get_center_info():
    #center_id = request.form["id"]
    center_id = request.args.get("id")

    search_center_sql = "SELECT `id`, `name`, `owner_id`, `latitude`, `longitude`, `image_path` FROM `food_center` WHERE `id`=" + center_id
    print(search_center_sql)

    db = connect_mysql_database()

    cursor = db.cursor()
    set_database_for_utf8(cursor)
    cursor.execute(search_center_sql)
    rows = cursor.fetchall()

    search_userid_sql = "SELECT `full_name` FROM `users` WHERE `id` LIKE '" + rows[0][2] + "'"
    cursor.execute(search_userid_sql)
    res = cursor.fetchall()

    center_info = {
        "id": rows[0][0],
        "name": rows[0][1],
        "owner_name": res[0][0],
        "latitude": rows[0][3],
        "longitude": rows[0][4],
        "image_path": rows[0][5]
        }

    search_cat_sql = "SELECT * FROM `cat_list` WHERE `belong_center`=" + center_id
    #print(search_content_sql)

    cursor.execute(search_cat_sql)
    rows = cursor.fetchall()

    cat_list = []
    for r in rows:
        search_userids_sql = "SELECT `full_name` FROM `users` WHERE `id`=" + str(r[4])
        print(search_userids_sql)
        cursor.execute(search_userid_sql)
        id_l = cursor.fetchall()

        cat_dict = {
		"id": r[0],
                "nickname": r[1],
                "belong_center": r[2],
                "user_id": id_l[0][0],
		"is_netural": r[5],
		"gender": r[6],
		"image_path": r[7]
                }
        print(cat_dict)
        cat_list.append(cat_dict)

    content_list = []

    search_content_sql = "SELECT * FROM `board` WHERE `belong_center_id`=" + center_id + " ORDER BY `created` DESC"
    print(search_content_sql)

    cursor.execute(search_content_sql)
    rows = cursor.fetchall()

    for b in rows:
        print(b)
        search_userid_sql = "SELECT `full_name` FROM `users` WHERE `id`=" + b[6]
        print(search_userid_sql)
        cursor.execute(search_userid_sql)
        res = cursor.fetchall()

        content_dict = {
                "id": b[0],
                "belong_center_id": b[1],
                "subject": b[2],
                "is_notice": b[3],
                "content": b[4],
                "created": str(b[5]),
                "user_id": res[0][0]
                }
        content_list.append(content_dict)

    disconnect_mysql_database(db)

    return json.dumps({"result": "ok", "cat_list_cnt": len(cat_list),
        "cat_list": cat_list, "center_info": center_info, "content_list_cnt":
        len(content_list), "content_list": content_list})


@api_bp.route("/center", methods=["GET"])
def center_get():
    search_id_sql = "SELECT `id`, `name`, `owner_id`, `latitude`, `longitude` FROM `food_center`"
    print(search_id_sql)

    db = connect_mysql_database()

    cursor = db.cursor()
    set_database_for_utf8(cursor)
    cursor.execute(search_id_sql)
    rows = cursor.fetchall()

    center_list = []
    for r in rows:
        search_userid_sql = "SELECT `full_name` FROM `users` WHERE `id` LIKE '" + r[2] + "'"
        cursor.execute(search_userid_sql)
        res = cursor.fetchall()

        rows_dict = {
                "id": r[0],
                "name": r[1],
                "owner_name": res[0][0],
                "latitude": r[3],
                "longitude": r[4]
                }
        #print((rows_dict))
        center_list.append(rows_dict)

    disconnect_mysql_database(db)

    return json.dumps({"result": "ok", "food_center_list": center_list})

@api_bp.route("/board", methods=["POST"])
def create_board_content():
    json_data = request.get_json()
    print(json_data)

    belong_center_id = json_data["belong_center_id"]
    subject = json_data["subject"]
    is_notice = json_data["is_notice"]
    content = json_data["content"]
    user_id = json_data["user_id"]

    db = connect_mysql_database()

    cursor = db.cursor()
    set_database_for_utf8(cursor)

    insert_sql = "INSERT INTO `board` (`belong_center_id`,`subject`, `is_notice`, `content`, `user_id`) \
            VALUES('{}', '{}', '{}', '{}', '{}')".format(belong_center_id, subject, is_notice, content, user_id)
    print(insert_sql)

    cursor.execute(insert_sql)
    db.commit()

    insert_id = cursor.lastrowid

    user_name_sql = "SELECT `full_name` FROM `users` WHERE `id`=" + user_id
    print(user_name_sql)
    cursor.execute(user_name_sql)
    user_name = cursor.fetchall()

    created_get_sql = "SELECT * FROM `board` WHERE `id`=" + str(insert_id)
    print(created_get_sql)

    cursor.execute(created_get_sql)
    insert_res = cursor.fetchall()

    disconnect_mysql_database(db)

    resp_dict = {
            "result": "ok",
            "content" : {
                "contents": insert_res[0][4],
                "is_notice": insert_res[0][3],
                "subject": insert_res[0][2],
                "user_id": user_name[0][0],
		"created": str(insert_res[0][5])
                }
            }

    return json.dumps(resp_dict)


@api_bp.route("/board", methods=["GET"])
def get_board_content():
    center_id = request.args.get("id")
    #print(center_id)

    search_content_sql = "SELECT * FROM `board` WHERE `belong_center_id`=" + center_id
    #print(search_content_sql)

    db = connect_mysql_database()

    cursor = db.cursor()
    set_database_for_utf8(cursor)
    cursor.execute(search_content_sql)
    rows = cursor.fetchall()

    #print(rows)

    content_list = []
    for r in rows:
        search_userid_sql = "SELECT `full_name` FROM `users` WHERE `id` LIKE '" + r[6] + "'"
        cursor.execute(search_userid_sql)
        res = cursor.fetchall()

        content_dict = {
                "id": r[0],
                "belong_center_id": r[1],
                "subject": r[2],
                "is_notice": r[3],
                "content": r[4],
                "created": str(r[5]),
                "user_id": r[6],
                "real_name": res[0][0]
                }
        print(content_dict)
        content_list.append(content_dict)

    disconnect_mysql_database(db)

    return json.dumps({"result": "ok", "content_list_cnt": len(content_list), "content_list": content_list})


@api_bp.route("/cat", methods=["POST"])
def create_cat():
    json_data = request.get_json(force=True)
    print(json_data)

    nickname = json_data["nickname"]
    belong_center = json_data["belong_center"]
    user_id = json_data["user"]
    is_netural = json_data["is_netural"]
    gender = json_data["gender"]
    #if json_data["image_path"] is not None:
    #    image_path = json_data["img_path"]

    search_res_sql = "SELECT * FROM `cat_list` WHERE `user_id` LIKE '" + user_id + "' AND `nickname` LIKE '" + nickname + "'"
    print(search_res_sql)

    db = connect_mysql_database()

    cursor = db.cursor()
    set_database_for_utf8(cursor)
    cursor.execute(search_res_sql)
    rows = cursor.fetchall()

    if rows:
        return json.dumps({"result": "error", "msg": "Duplicated data found"})

    insert_sql = "INSERT INTO `cat_list` (`nickname`,`belong_center`, `user_id`, `is_netural`, `gender`) \
            VALUES('{}', '{}', '{}', '{}', '{}')".format(nickname, belong_center, user_id, is_netural, gender)
    print(insert_sql)

    cursor.execute(insert_sql)
    db.commit()

    cat_id = cursor.lastrowid

    cat_image_update_sql = "UPDATE `cat_list` SET `image_path`='http://" + IMG_URL +"/upload/cat_img_" + str(cat_id) + ".jpg' WHERE `id`=" + str(cat_id)
    print(cat_image_update_sql)

    cursor.execute(cat_image_update_sql)
    db.commit()

    disconnect_mysql_database(db)

    return json.dumps({"result": "ok", "cat_id": cat_id})

@api_bp.route("/cat", methods=["GET"])
def get_cat_list():
    center_id = request.form["id"]
    #print(center_id)

    search_cat_sql = "SELECT * FROM `cat_list` WHERE `belong_center`=" + center_id
    #print(search_content_sql)

    db = connect_mysql_database()

    cursor = db.cursor()
    set_database_for_utf8(cursor)
    cursor.execute(search_cat_sql)
    rows = cursor.fetchall()

    disconnect_mysql_database(db)

    print(rows)

    cat_list = []
    for r in rows:
        cat_dict = {
		"id": r[0],
                "nickname": r[1],
                "belong_center": r[2],
                "user_id": r[3],
		"is_netural": r[5],
		"gender": r[6],
		"image_path": r[7]
                }
        print(cat_dict)
        cat_list.append(cat_dict)

    return json.dumps({"result": "ok", "cat_list_cnt": len(cat_list)  ,"cat_list": cat_list})

