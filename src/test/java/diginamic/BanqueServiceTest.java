package diginamic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import diginamic.entites.Compte;
import diginamic.exception.BanqueException;

public class BanqueServiceTest {

    private CompteDao cmptDao;
    private TransactionDao trDao;
    private TransactionProcessor trp;
    private BanqueService banqueS;

    @BeforeEach
    void setUp() {
        cmptDao = Mockito.mock(CompteDao.class);
        trDao = Mockito.mock(TransactionDao.class);
        trp = Mockito.spy(TransactionProcessor.class);
        banqueS = new BanqueService();

        banqueS.setCompteDao(cmptDao);
        banqueS.setTransactionProcessor(trp);
        banqueS.setTransactionDao(trDao);

        Mockito.doNothing().when(trp).envoyerMailConfirmation(Mockito.any(Compte.class), Mockito.anyString());
    }

    @Test
    void testCreerCompteExistant() {
        Mockito.when(cmptDao.findByNumero(Mockito.anyString())).thenReturn(new Compte());
        assertThrows(BanqueException.class, () -> {
            banqueS.creerCompte("ibra", 200, "ibra");
        }, "exception");
    }

    @Test
    void testCreerCompteNotExiste() {
        Mockito.when(cmptDao.findByNumero("FR24")).thenReturn(null);
        try {
            Compte c = banqueS.creerCompte("FR24", 200, "ibra");
            assertEquals("FR24", c.getNumero());
            assertEquals(200, c.getSolde());
            assertEquals("ibra", c.getEmail());
        } catch (BanqueException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDeposer() {
        Compte c = new Compte();
        c.setSolde(100);
        c.setNumero("FR24");
        banqueS.deposer(c, 200);
        assertEquals(0, trp.getErrors().size());
        assertEquals(300, c.getSolde());
    }

    @Test
    void testRetirerSoldeSuffisant() {
        Compte c = new Compte();
        c.setSolde(100);
        c.setNumero("FR24");
        banqueS.retirer(c, 50);
        assertEquals(0, trp.getErrors().size());
        assertEquals(50, c.getSolde());
    }

    @Test
    void testRetirerSoldeInsuffisant() {
        Compte c = new Compte();
        c.setSolde(100);
        c.setNumero("FR24");
        banqueS.retirer(c, 101);
        assertEquals(1, trp.getErrors().size());
        assertEquals(100, c.getSolde());
    }

    @Test
    void testVirerSoldeSuffisant() {
        Compte c1 = new Compte();
        c1.setSolde(100);
        c1.setNumero("FR24");
        
        Compte c2 = new Compte();
        c2.setSolde(100);
        c2.setNumero("FR25");

        banqueS.virer(c1, c2, 50);

        assertEquals(0, trp.getErrors().size());
        assertEquals(49.5, c1.getSolde());
        assertEquals(150, c2.getSolde());
    }

    @Test
    void testVirerSoldeInsuffisant() {
        Compte c1 = new Compte();
        c1.setSolde(100);
        c1.setNumero("FR24");

        Compte c2 = new Compte();
        c2.setSolde(100);
        c2.setNumero("FR25");

        banqueS.virer(c1, c2, 150);

        assertEquals(1, trp.getErrors().size());
        assertEquals(100, c1.getSolde());
        assertEquals(100, c2.getSolde());
    }
}
