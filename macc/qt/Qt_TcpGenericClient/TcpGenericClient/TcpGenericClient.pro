#-------------------------------------------------
#
# Project created by QtCreator 2018-04-12T16:02:27
#
#-------------------------------------------------

QT       += core gui network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = TcpGenericClient
TEMPLATE = app


SOURCES += main.cpp\
        mainwindow.cpp \
    clienttcp.cpp

HEADERS  += mainwindow.h \
    clienttcp.h

FORMS    += mainwindow.ui
