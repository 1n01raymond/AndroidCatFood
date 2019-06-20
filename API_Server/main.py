#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import Flask, Blueprint, request, render_template, redirect, url_for
from werkzeug.utils import secure_filename
from api import api_bp
import MySQLdb
import json
import os

UPLOAD_FOLDER = "/var/www/html/upload/"

IMAGE_URL = "BLANK_URL"

app = Flask(__name__)

app.register_blueprint(api_bp, url_prefix="/api/")
app.config["UPLOAD_FOLDER"] = UPLOAD_FOLDER

ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/upload/upload_cat', methods=['GET', 'POST'])
def upload_cat_img():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']
        # if user does not select file, browser also
        # submit a empty part without filename
        if file.filename == '':
            flash('No selected file')
            return redirect(request.url)
        if file and allowed_file(file.filename):
            filename = "cat_" + request.args.get("id") + "_" + secure_filename(file.filename)
            print(filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

            ret_dict = {
                    "result": "ok",
                    "filename": str(filename),
		    "cat_id": request.args.get("id")
                    }

            db = connect_mysql_database()
            cursor = db.cursor()
            set_database_for_utf8(cursor)

            cat_image_update_sql = "UPDATE `cat_list` SET `image_path`='http://" + URL + "/upload/" + filename + "' WHERE `id`=" + request.args.get("id")
            print(cat_image_update_sql)
            cursor.execute(cat_image_update_sql)
            db.commit()

            return json.dumps(ret_dict)
    return '''
    <!doctype html>
    <title>Upload New Cat Image</title>
    <h1>Upload New Cat Image</h1>
    <form method=post enctype=multipart/form-data>
      <p><input type=file name=file>
         <input type=submit value=Upload>
    </form>
    '''

@app.route('/upload/upload_center', methods=['GET', 'POST'])
def upload_center_img():
    if request.method == 'POST':
        #print(request.args.get("id"))
        # check if the post request has the file part
        if 'file' not in request.files:
            flash('No file part')
            return redirect(request.url)
        file = request.files['file']
        # if user does not select file, browser also
        # submit a empty part without filename
        if file.filename == '':
            flash('No selected file')
            return redirect(request.url)
        if file and allowed_file(file.filename):
            filename = "center_" + request.args.get("id") + "_" + secure_filename(file.filename)
            print(filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

            ret_dict = {
                    "result": "ok",
                    "filename": str(filename),
                    "center_id": request.args.get("id")
                    }

            db = connect_mysql_database()
            cursor = db.cursor()
            set_database_for_utf8(cursor)

            cat_image_update_sql = "UPDATE `food_center` SET `image_path`='http://" + IMAGE_URL + "/upload/" + filename + "' WHERE `id`=" + request.args.get("id")
            print(cat_image_update_sql)
            cursor.execute(cat_image_update_sql)
            db.commit()


            return json.dumps(ret_dict)
    return '''
    <!doctype html>
    <title>Upload New Center Image</title>
    <h1>Upload New Center Image</h1>
    <form method=post enctype=multipart/form-data>
      <p><input type=file name=file>
         <input type=submit value=Upload>
    </form>
    '''

###
# MySQL DB 접속 설정
###
mysql_host = "localhost"
mysql_userid = "DB_USER"
mysql_password = "DB_PASSWORD"
mysql_dbname = "DB_NAME"

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

if __name__ == '__main__':
    app.run(debug=False, host='0.0.0.0')

