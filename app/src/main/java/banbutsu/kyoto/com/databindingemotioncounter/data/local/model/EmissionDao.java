package banbutsu.kyoto.com.databindingemotioncounter.data.local.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by Yasuaki on 2018/01/17.
 */
@Dao
public interface EmissionDao {

  @Insert
  void insert(EmissionEntry emissionEntry);

  @Update
  void update(EmissionEntry... emissionEntry);

  @Query("SELECT * FROM emissions WHERE characterId = :characterId AND date = :date")
  EmissionEntry findEmissionByCharacterAndDate(int characterId, long date);
}