package diginamic;

import java.util.List;

import diginamic.entites.Compte;
import diginamic.entites.Statut;
import diginamic.exception.BanqueException;

/**
 * Classe de service bancaire
 */
public class BanqueService {
	
    /** Effectue les opérations bancaire */
    private TransactionProcessor transactionProcessor;
    
    /** Stocke les transactions en erreur en base de données */
    private TransactionDao transactionDao;

	/** Stocke les comptes en base de données */
	private CompteDao compteDao;

	/**
	 * Crée un compte avec un nouveau numéro de compte. Si ce compte existe déjà une exception est lancée.
	 * @param numero numero du compte
	 * @param solde solde
	 * @param email email
	 * @return Compte
	 * @throws BanqueException si le compte existe déjà
	 */

	
	public Compte creerCompte(String numero, double solde, String email) throws BanqueException  {
		Compte compte = compteDao.findByNumero(numero);
		if (compte!=null){
			throw new BanqueException("Le compte avec le numéro "+numero+" existe déjà.");
		}
		// Création du compte en base de données
		compte = new Compte(numero, email, solde);
		compteDao.enregistrer(compte);
		return compte;
	}

	/** Dépose un montant sur un compte
     * @param compte compte à créditer
     * @param montant montant
     */
    public void deposer(Compte compte, double montant) {
    	if (transactionProcessor.effectuerDepot(compte, montant)) {
			// Mise à jour du solde du compte
			compteDao.mettreAJour(compte);
    	}
    	else {
    		traiterErreur(compte, transactionProcessor.getErrors());
    	}
    }

    /** Effectue un retrait sur un compte
     * @param compte compte à débiter
     * @param montant montant
     */
    public void retirer(Compte compte, double montant) {
        if (transactionProcessor.effectuerRetrait(compte, montant)) {
			compteDao.mettreAJour(compte);
        } 
        else {
    		traiterErreur(compte, transactionProcessor.getErrors());
    	}
    }

    /** Effectue un virement d'un compte vers un bénéficiaire
     * @param origine compte depuis lequel le virement s'effectue
     * @param beneficiaire compte bénéficiaire du virement
     * @param montant montant
     */
    public void virer(Compte origine, Compte beneficiaire, double montant) {

        if (transactionProcessor.effectuerVirement(origine, beneficiaire, montant)) {
			compteDao.mettreAJour(origine);
			compteDao.mettreAJour(beneficiaire);
        } 
        else {
    		traiterErreur(origine, transactionProcessor.getErrors());
    	}
    }

	/** Traite les transactions en erreur
	 * @param compte compte
	 * @param errors liste des erreurs constatées
	 */
	private void traiterErreur(Compte compte, List<String> errors) {
		
		for (String error : errors) {
			transactionDao.enregistrer(compte, 0.0, error, Statut.ECHEC);
		}
	}

	/**
	 * @return the transactionProcessor
	 */
	public TransactionProcessor getTransactionProcessor() {
		return transactionProcessor;
	}

	/**
	 * @param transactionProcessor the transactionProcessor to set
	 */
	public void setTransactionProcessor(TransactionProcessor transactionProcessor) {
		this.transactionProcessor = transactionProcessor;
	}

	/**
	 * Getter
	 *
	 * @return compteDao
	 */
	public CompteDao getCompteDao() {
		return compteDao;
	}

	/**
	 * Setter
	 *
	 * @param compteDao compteDao
	 */
	public void setCompteDao(CompteDao compteDao) {
		this.compteDao = compteDao;
	}

	/**
	 * Getter
	 *
	 * @return transactionDao
	 */
	public TransactionDao getTransactionDao() {
		return transactionDao;
	}

	/**
	 * Setter
	 *
	 * @param transactionDao transactionDao
	 */
	public void setTransactionDao(TransactionDao transactionDao) {
		this.transactionDao = transactionDao;
	}
}