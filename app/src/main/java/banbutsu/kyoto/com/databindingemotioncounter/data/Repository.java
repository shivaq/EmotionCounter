package banbutsu.kyoto.com.databindingemotioncounter.data;

import android.arch.lifecycle.LiveData;
import banbutsu.kyoto.com.databindingemotioncounter.MyExecutor;
import banbutsu.kyoto.com.databindingemotioncounter.data.local.PreferencesHelper;
import banbutsu.kyoto.com.databindingemotioncounter.data.local.model.CharacterDao;
import banbutsu.kyoto.com.databindingemotioncounter.data.local.model.CharacterEntry;
import banbutsu.kyoto.com.databindingemotioncounter.data.local.model.EmotionDao;
import banbutsu.kyoto.com.databindingemotioncounter.data.local.model.EmotionEntry;
import banbutsu.kyoto.com.databindingemotioncounter.data.local.model.MonologueDao;
import banbutsu.kyoto.com.databindingemotioncounter.data.local.model.MonologueEntry;
import banbutsu.kyoto.com.databindingemotioncounter.data.local.model.RemarkDao;
import banbutsu.kyoto.com.databindingemotioncounter.data.local.model.RemarkEntry;
import banbutsu.kyoto.com.databindingemotioncounter.utils.DbUtility;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Yasuaki on 2018/01/14.
 */
@Singleton
public class Repository {

  private final PreferencesHelper preferencesHelper;
  private final CharacterDao characterDao;
  private final MonologueDao monologueDao;
  private final EmotionDao emotionDao;

  private final RemarkDao remarkDao;
  private final MyExecutor executor;

  @Inject
  public Repository(
      PreferencesHelper preferencesHelper,
      CharacterDao characterDao,
      MonologueDao monologueDao, EmotionDao emotionDao,
      RemarkDao remarkDao, MyExecutor executor) {
    this.preferencesHelper = preferencesHelper;
    this.characterDao = characterDao;
    this.monologueDao = monologueDao;
    this.emotionDao = emotionDao;
    this.remarkDao = remarkDao;
    this.executor = executor;
  }

  ////////////////////// Remark ///////////////////////////
  public LiveData<List<RemarkEntry>> getRemarkByEmotion(String emotion) {
    return remarkDao.getRemarkByEmotion(emotion);
  }

  public void insertRemark(String emotion, String say) {
    executor.diskIO().execute(() -> remarkDao.insert(new RemarkEntry(emotion, say)));

  }

  public void updateRemark(String emotion, String say, long remarkId) {
    executor.diskIO().execute(() -> remarkDao.update(emotion, say, remarkId));
  }

  public void deleteRemark(long remarkId) {
    executor.diskIO().execute(() -> remarkDao.delete(remarkId));
  }

  ////////////////////// Monologue ///////////////////////////
  public void createNewMonologue() {
    long emotionId = emotionDao.insert(
        new EmotionEntry(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0));
    monologueDao.insert(
        new MonologueEntry(System.currentTimeMillis(), preferencesHelper.retrieveCurrentCharacter(),
            (int) emotionId));
  }

  /*********************  日またぎ チェック *****************************/
  public void firstLaunchCheck() {
    // 初ローンチなら デフォルトデータをインサート
    if (preferencesHelper.isFirstLaunch()) {
      executor.diskIO().execute(() -> {
        long characterId = characterDao.insert(new CharacterEntry("私", 1));
        preferencesHelper.putCurrentCharacter((int) characterId);
        createNewMonologue();
        remarkDao.bulkInsert(DbUtility.getDefaultRemarks());
      });
    }
  }

  public long retrieveTomorrow() {
    return preferencesHelper.retrieveTomorrow();
  }

  public void putTomorrow() {
    preferencesHelper.putTomorrow();
  }

  public MonologueEntry getMonologueById() {

    return monologueDao.getMonologueById(preferencesHelper.retrieveMonologueId());
  }



}