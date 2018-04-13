#include <QMdiArea>
#include <QWidget>
#include <QApplication>
#include <QDesktopWidget>
#include <QMdiSubWindow>

#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "clienttcp.h"

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    //https://qt.developpez.com/faq/?page=modules-qtgui-affichage-fenetres
    int x = QApplication::desktop()->availableGeometry(this).width() /2 - frameSize().width()/2;
    int y = QApplication::desktop()->availableGeometry(this).height() /2 - frameSize().height()/2;
    move(x,y);

    zoneCentrale = new QMdiArea();
    zoneCentrale->setHorizontalScrollBarPolicy(Qt::ScrollBarAsNeeded);
    zoneCentrale->setVerticalScrollBarPolicy(Qt::ScrollBarAsNeeded);
    setCentralWidget(zoneCentrale);

    QAction *newAct = new QAction(tr("&Nouveau"), this);
    newAct->setShortcuts(QKeySequence::New);
    newAct->setStatusTip(tr("Nouveau client"));
    connect(newAct, SIGNAL(triggered()), this, SLOT(pslot_newClient()));

    QMenu *fileMenu=menuBar()->addMenu(tr("&Client"));
    fileMenu->addAction(newAct);
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::pslot_newClient(void)
{

    QWidget *qw_tmp = new QWidget;
    ClientTcp *unClientTcp = new ClientTcp(qw_tmp);
    Q_UNUSED(unClientTcp);
    QMdiSubWindow * subWindow = zoneCentrale->addSubWindow(qw_tmp);
    qw_tmp->setVisible(true);

    subWindow->installEventFilter(unClientTcp);
}
