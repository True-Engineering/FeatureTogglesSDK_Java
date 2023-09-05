package ru.trueengineering.feature.flag.starter.provider;

import org.springframework.lang.NonNull;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagsStateWithHash;

import java.util.Map;

/**
 * @author s.sharaev
 */
public interface FeatureFlagStateProvider {

    /**
     * Возвращает состояние указанного фиче-флага
     *
     * @param featureName название фиче-флага
     * @return состояние фиче-флага. <code>true</code> - если включен, <code>false</code> -
     * если выключен или не существует
     */
    boolean check(@NonNull String featureName);

    /**
     * Возвращает состояния фиче-флагов с указанным тегом
     *
     * @param tag название тега
     * @return Мапу состояний фиче-флагов с указанным тегом. Ключ - uid фиче-флага, значение - его состояние.
     * <code>true</code> - если включен, <code>false</code> - если выключен
     */
    Map<String, Boolean> getByTag(String tag);

    /**
     * Возвращает состояния фиче-флагов с указанным тегом и hash всех фичифлагов
     *
     * @param tag название тега
     * @return Hash всех фичифлагов и мапу состояний фиче-флагов с указанным тегом. Ключ - uid фиче-флага, значение -
     * его состояние.
     * <code>true</code> - если включен, <code>false</code> - если выключен
     */
    FeatureFlagsStateWithHash getWithHashByTag(String tag);
}
