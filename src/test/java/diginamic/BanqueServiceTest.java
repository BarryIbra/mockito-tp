package diginamic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import diginamic.entites.Compte;
import diginamic.exception.BanqueException;

public class BanqueServiceTest {
    @Test
    void testCreerCompteExistant() {
        CompteDao cmptDao=Mockito.mock(CompteDao.class);
        BanqueService banqueS=new BanqueService();
        banqueS.setCompteDao(cmptDao);
        Mockito.when(cmptDao.findByNumero(Mockito.anyString())).thenReturn(new Compte());
        assertThrows(BanqueException.class, ()->{banqueS.creerCompte("ibra",200,"ibra");},"exception");
    

    }

    @Test

    void testCreerCompteNotExiste() {
        CompteDao cmptDao=Mockito.mock(CompteDao.class);
        BanqueService banqueS=new BanqueService();
        banqueS.setCompteDao(cmptDao);
        Mockito.when(cmptDao.findByNumero("FR24")).thenReturn(null);
        try {
            Compte c=banqueS.creerCompte("FR24", 200, "ibra");
            assertEquals("FR24", c.getNumero());
            assertEquals(200, c.getSolde());
            assertEquals("ibra",c.getEmail());
        } catch (BanqueException e) {
            
            e.printStackTrace();
        }
        
    

    }

    // @Test
    // void testDeposer() {
    //     CompteDao cmptDao=Mockito.mock(CompteDao.class);
    //     TransactionProcessor trp=Mockito.spy(TransactionProcessor.class);
    //     Mockito.doReturn(false).when(trp).effectuerDepot(Mockito.any(Compte.class), Mockito.anyDouble());
    //     BanqueService banqueS=new BanqueService();
    //     banqueS.setCompteDao(cmptDao);
    //     banqueS.setTransactionProcessor(trp);
    //     Compte c=new Compte();
    //     banqueS.deposer(c, 200);
    //     assertEquals(0, trp.getErrors().size());
        
        


        
    }

    @Test
    void testRetirer() {
        
    }

    @Test
    void testVirer() {
        
    }


}
