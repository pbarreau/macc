#ifndef CLIENTTCP_H
#define CLIENTTCP_H

#include <QObject>
#include <QtNetwork>
#include <QGridLayout>
#include <QGroupBox>
#include <QLineEdit>

class ClientTcp : public QObject
{
    Q_OBJECT
public:
    explicit ClientTcp(QObject *parent = 0);
    ~ClientTcp();

private:
    void monLayout_NouveauClient(QWidget *fen);

public slots:
    void recoit_IP(QString IP2);  // en provenance de l'IHM et se connecte au serveur
    void emmettreVersServeur(QString t); // en provenance de l'IHM et écrit sur la socket

private slots :
    void slot_openSrv(bool);
    void slot_endOfTests(bool);
    void slot_sendToSrv(bool);
    void slot_connexionOK();  // en provenance de la socket et émet un signal vers l'IHM
    void slot_readFromSrv();       // en provenance de la socket, lit la socket, émet un signal vers l'IHM

signals:
    void vers_IHM_connexion_OK();
    void vers_IHM_texte(QString);

private :
    static int total;
    QWidget *fen;
    QGroupBox *gb_srvTest;
    QGroupBox *gb_srvConf;
    QLineEdit *le_srvIp;
    QLineEdit *le_srvPort;
    QLineEdit *le_showMsg;
    QLineEdit *le_sendMsg;
    QString IP;
    int port;
    QTcpSocket *soc;
};

#endif // CLIENTTCP_H
