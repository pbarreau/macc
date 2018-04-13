#include <QWidget>
#include <QMdiArea>
#include <QGroupBox>
#include <QLineEdit>
#include <QFormLayout>
#include <QRect>
#include <QPushButton>
#include <QTextStream>

#include "clienttcp.h"

int ClientTcp::total = 0;

ClientTcp::ClientTcp(QObject *parent) : QObject(parent)
{
    fen = qobject_cast<QWidget*>(parent);

    monLayout_NouveauClient(fen);

    total++;
    QString ClientVisualId = "TcpClient_" + QString::number(total);
    fen->setWindowTitle(ClientVisualId);
}

ClientTcp::~ClientTcp()
{
}

void ClientTcp::monLayout_NouveauClient(QWidget *fen)
{
    QGridLayout *vbl_main = new QGridLayout;

    gb_srvConf = new QGroupBox(tr("Serveur cible"));
    gb_srvTest = new QGroupBox(tr("Tests"));

    QVBoxLayout *vbl_top = new QVBoxLayout;
    le_srvIp = new QLineEdit;
    le_srvPort = new QLineEdit;
    QFormLayout *fly_srvInfo = new QFormLayout;
    QPushButton *qpb = new QPushButton("Connection");

    QGridLayout *gdl_msg = new QGridLayout;
    le_sendMsg = new QLineEdit;
    le_showMsg = new QLineEdit;

    QFormLayout *fly_send_1 = new QFormLayout;
    QFormLayout *fly_send_2 = new QFormLayout;
    QPushButton *qpb_send = new QPushButton("Envoyer");
    QPushButton *qpb_quit = new QPushButton("Quitter");

    soc = NULL; // Pas de socket pour l'instant

    fly_srvInfo->addRow("Ip:",le_srvIp);
    fly_srvInfo->addRow("Port:",le_srvPort);
    vbl_top->addLayout(fly_srvInfo);
    vbl_top->addWidget(qpb);
    gb_srvConf->setLayout(vbl_top);

    fly_send_1->addRow("Msg:",le_sendMsg);
    fly_send_2->addRow("Rsp:",le_showMsg);
    gdl_msg->addLayout(fly_send_1,0,0);
    gdl_msg->addLayout(fly_send_2,1,0);
    gdl_msg->addWidget(qpb_send,0,1);
    gdl_msg->addWidget(qpb_quit,2,0,1,2);
    gb_srvTest->setLayout(gdl_msg);

    le_srvPort->setText("1024");
    le_showMsg->setEnabled(false);
    le_showMsg->setText("en attente...");

    gb_srvTest->setEnabled(false);
    gb_srvTest->setVisible(false);

    vbl_main->addWidget(gb_srvConf,0,0);
    vbl_main->addWidget(gb_srvTest,1,0);

    connect(qpb,SIGNAL(clicked(bool)),this,SLOT(slot_openSrv(bool)));
    connect(qpb_send,SIGNAL(clicked(bool)),this,SLOT(slot_sendToSrv(bool)));
    connect(qpb_quit,SIGNAL(clicked(bool)),this,SLOT(slot_endOfTests(bool)));


    fen->setLayout(vbl_main);
    fen->setFixedSize(250,250);
}


void ClientTcp::slot_openSrv(bool click)
{
    Q_UNUSED(click);
    QString srv = le_srvIp->text();
    int port = le_srvPort->text().toInt();
    soc = new QTcpSocket;

    connect(soc,SIGNAL(connected()),this,SLOT(slot_connexionOK()));
    soc->connectToHost(srv,port); // pour se connecter au serveur
}

void ClientTcp::slot_connexionOK()
{
    gb_srvTest->setVisible(true);
    gb_srvTest->setEnabled(true);
    gb_srvConf->setEnabled(false);
    QObject::connect(soc, SIGNAL(readyRead()), this, SLOT(slot_readFromSrv()));

    emit vers_IHM_connexion_OK(); // on envoie un signal à l'IHM
}

void ClientTcp::slot_sendToSrv(bool click)
{
    Q_UNUSED(click);
    QString msg = le_sendMsg->text();
    emmettreVersServeur(msg);
}

void ClientTcp::emmettreVersServeur(QString t)
{
    QTextStream texte(soc); // on associe un flux à la socket
    texte <<t<<endl;        // on écrit dans le flux le texte saisi dans l'IHM
}

void ClientTcp::slot_readFromSrv()
{
    QString ligne;
    while(soc->canReadLine()) // tant qu'il y a quelque chose à lire dans la socket
    {
        ligne = soc->readLine();     // on lit une ligne
        emit vers_IHM_texte(ligne); // on envoie à l'IHM
    }
    le_showMsg->setText(ligne);
}

void ClientTcp::slot_endOfTests(bool click)
{
    Q_UNUSED(click);
    if(soc!=NULL){
        soc->close();
        delete soc;
    }
    le_sendMsg->setText("");
    le_showMsg->setText("en attente...");
    gb_srvTest->setEnabled(false);
    gb_srvConf->setEnabled(true);
}

bool ClientTcp::eventFilter(QObject *obj, QEvent *e)
{
    switch (e->type())
     {
         case QEvent::Close:
         {
             QMdiSubWindow * subWindow = (QMdiSubWindow*)(obj);
             Q_ASSERT (subWindow != NULL);

             if(soc!=NULL){
                 soc->close();
                 delete soc;
             }

             break;
         }
         default:
             qt_noop();
     }
     return QObject::eventFilter(obj, e);
}
