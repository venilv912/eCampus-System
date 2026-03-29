package com.ecampus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecampus.model.*;

@Repository
public interface SlotPreferencesRepository extends JpaRepository<SlotPreferences, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM ec2.slotpreferences slotPref WHERE slotPref.SID = :sid", nativeQuery = true)
    int deleteBySid(@Param("sid") Long sid);

    @Query(value = "SELECT COALESCE(MAX(slotPref.sp_id), 0) FROM ec2.slotpreferences slotPref", nativeQuery = true)
    Long findMaxId();

    @Query(value = "SELECT slotPref.slot as slot, slotPref.pref_index as index FROM ec2.slotpreferences slotPref WHERE slotPref.SID = :sid", nativeQuery = true)
    List<Object[]> getBySid(@Param("sid") Long sid);

    @Query(value = "SELECT st.stdinstid, sp.slot, sp.pref_index FROM ec2.students st RIGHT JOIN ec2.slotpreferences sp ON st.stdid = sp.sid", nativeQuery = true)
    List<Object[]> getSlotPrefForElectiveRegistration();

}
