package com.askfast.askfastapi;

import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import com.askfast.model.Adapter;
import com.askfast.model.DDRRecord;
import com.askfast.model.Dialog;


public class AskFastRestClientTest extends TestFramework {

    @Test
    public void testBuyingAdapter()
    {
        AskFastRestClient client = new AskFastRestClient(accountId, refreshToken, accessToken);
        
        // Check the free adapters
        Set<Adapter> adapters = client.getFreeAdapters(null, null);
        LOG.info("Found "+adapters.size()+" free adapters");
        Assert.assertTrue(adapters.size() > 0);
        
        // Check the owned adapters
        Set<Adapter> ownAdapters = client.getAdapters(null);
        LOG.info("Found "+ownAdapters.size()+" own adapters");
        Assert.assertTrue(ownAdapters.size() == 0);
        
        // Buy an adapter
        Adapter freeAdapter = adapters.iterator().next();
        String adapterId = freeAdapter.getConfigId();
        client.buyAdapter(adapterId);
        
        // Check the owned adapters again
        ownAdapters = client.getAdapters("broadsoft");
        LOG.info("Found "+ownAdapters.size()+" own adapters");
        Assert.assertTrue(ownAdapters.size() == 1);
        
        // Remove the adapter
        client.removeAdapter(adapterId);
        
        // Check the owned adapters last time
        ownAdapters = client.getAdapters(null);
        LOG.info("Found "+ownAdapters.size()+" own adapters");
        Assert.assertTrue(ownAdapters.size() == 0);
    }
    
    @Test
    public void testCreatingDialog()
    {
        AskFastRestClient client = new AskFastRestClient(accountId, refreshToken, accessToken);
        
        // Check Dialog count
        Set<Dialog> dialogs = client.getDialogs();
        LOG.info("Found "+dialogs.size()+" dialogs");
        Assert.assertTrue(dialogs.size() == 0);
        
        String name = "Test Dialog";
        String url = "http://test.me/";
        
        // Create dialog
        Dialog newDialog = client.createDialog(new Dialog(name, url));
        String dialogId = newDialog.getId();        
        
        // Check values of new dialog
        Dialog dialog = client.getDialog(dialogId); 
        Assert.assertEquals(name, dialog.getName());
        Assert.assertEquals(url, dialog.getUrl());
        
        // Check Dialog count
        dialogs = client.getDialogs();
        LOG.info("Found "+dialogs.size()+" dialogs");
        Assert.assertTrue(dialogs.size() == 1);
        
        // Update Dialog
        String newName = "Test Dialog 2";
        String newUrl = "http://test.me/2";
        Dialog updatedDialog = client.updateDialog(dialogId, new Dialog(newName, newUrl));
        Assert.assertEquals(newName, updatedDialog.getName());
        Assert.assertEquals(newUrl, updatedDialog.getUrl());
        
        // Check new values of new dialog
        dialog = client.getDialog(dialogId); 
        Assert.assertEquals(newName, dialog.getName());
        Assert.assertEquals(newUrl, dialog.getUrl());
        
        // Remove dialog
        client.removeDialog(dialogId);
        
        // Check Dialog count
        dialogs = client.getDialogs();
        LOG.info("Found "+dialogs.size()+" dialogs");
        Assert.assertTrue(dialogs.size() == 0);
    }
    
    @Test
    public void testReadingDDRRecords()
    {
        AskFastRestClient client = new AskFastRestClient(accountId, refreshToken, accessToken);
        List<DDRRecord> ddrs = client.getDDRRecords(null,  null,  null, null, null, null, null, null, null, null);
        
        LOG.info("Found "+ddrs.size()+" ddrs");
        Assert.assertTrue(ddrs.size() > 0);
        
        ddrs = client.getDDRRecords(null, null,  null, null, System.currentTimeMillis(), null, null, null, null, null);
        Assert.assertTrue(ddrs.size() == 0);
    }
}
