package com.example.usagetracker.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppUsageDao_Impl implements AppUsageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AppUsageEntity> __insertionAdapterOfAppUsageEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateControlStatus;

  public AppUsageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAppUsageEntity = new EntityInsertionAdapter<AppUsageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `app_usage` (`id`,`packageName`,`appName`,`usageTimeInMillis`,`date`,`isControlled`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppUsageEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getPackageName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getPackageName());
        }
        if (entity.getAppName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getAppName());
        }
        statement.bindLong(4, entity.getUsageTimeInMillis());
        statement.bindLong(5, entity.getDate());
        final int _tmp = entity.isControlled() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__preparedStmtOfUpdateControlStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE app_usage SET isControlled = ? WHERE packageName = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertUsage(final AppUsageEntity usage,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAppUsageEntity.insert(usage);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAppControlStatus(final String packageName, final boolean isControlled,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> AppUsageDao.super.updateAppControlStatus(packageName, isControlled, __cont), $completion);
  }

  @Override
  public Object updateControlStatus(final String packageName, final boolean isControlled,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateControlStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isControlled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        if (packageName == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, packageName);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateControlStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AppUsageEntity>> getUsageByTimeRange(final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM app_usage WHERE date >= ? AND date <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_usage"}, new Callable<List<AppUsageEntity>>() {
      @Override
      @NonNull
      public List<AppUsageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfUsageTimeInMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "usageTimeInMillis");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIsControlled = CursorUtil.getColumnIndexOrThrow(_cursor, "isControlled");
          final List<AppUsageEntity> _result = new ArrayList<AppUsageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppUsageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final long _tmpUsageTimeInMillis;
            _tmpUsageTimeInMillis = _cursor.getLong(_cursorIndexOfUsageTimeInMillis);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final boolean _tmpIsControlled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsControlled);
            _tmpIsControlled = _tmp != 0;
            _item = new AppUsageEntity(_tmpId,_tmpPackageName,_tmpAppName,_tmpUsageTimeInMillis,_tmpDate,_tmpIsControlled);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AppUsageEntity>> getAppUsageByTimeRange(final String packageName,
      final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM app_usage WHERE packageName = ? AND date >= ? AND date <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (packageName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, packageName);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_usage"}, new Callable<List<AppUsageEntity>>() {
      @Override
      @NonNull
      public List<AppUsageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfUsageTimeInMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "usageTimeInMillis");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIsControlled = CursorUtil.getColumnIndexOrThrow(_cursor, "isControlled");
          final List<AppUsageEntity> _result = new ArrayList<AppUsageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppUsageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final long _tmpUsageTimeInMillis;
            _tmpUsageTimeInMillis = _cursor.getLong(_cursorIndexOfUsageTimeInMillis);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final boolean _tmpIsControlled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsControlled);
            _tmpIsControlled = _tmp != 0;
            _item = new AppUsageEntity(_tmpId,_tmpPackageName,_tmpAppName,_tmpUsageTimeInMillis,_tmpDate,_tmpIsControlled);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ControlledAppInfo>> getControlledApps() {
    final String _sql = "SELECT DISTINCT packageName, appName, isControlled FROM app_usage WHERE isControlled = 1 GROUP BY packageName";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_usage"}, new Callable<List<ControlledAppInfo>>() {
      @Override
      @NonNull
      public List<ControlledAppInfo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = 0;
          final int _cursorIndexOfAppName = 1;
          final int _cursorIndexOfIsControlled = 2;
          final List<ControlledAppInfo> _result = new ArrayList<ControlledAppInfo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ControlledAppInfo _item;
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final boolean _tmpIsControlled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsControlled);
            _tmpIsControlled = _tmp != 0;
            _item = new ControlledAppInfo(_tmpPackageName,_tmpAppName,_tmpIsControlled);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAppByPackageName(final String packageName,
      final Continuation<? super AppUsageEntity> $completion) {
    final String _sql = "SELECT * FROM app_usage WHERE packageName = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (packageName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, packageName);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AppUsageEntity>() {
      @Override
      @Nullable
      public AppUsageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfUsageTimeInMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "usageTimeInMillis");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfIsControlled = CursorUtil.getColumnIndexOrThrow(_cursor, "isControlled");
          final AppUsageEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final String _tmpAppName;
            if (_cursor.isNull(_cursorIndexOfAppName)) {
              _tmpAppName = null;
            } else {
              _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            }
            final long _tmpUsageTimeInMillis;
            _tmpUsageTimeInMillis = _cursor.getLong(_cursorIndexOfUsageTimeInMillis);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final boolean _tmpIsControlled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsControlled);
            _tmpIsControlled = _tmp != 0;
            _result = new AppUsageEntity(_tmpId,_tmpPackageName,_tmpAppName,_tmpUsageTimeInMillis,_tmpDate,_tmpIsControlled);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAppNameByPackage(final String packageName,
      final Continuation<? super String> $completion) {
    final String _sql = "SELECT appName FROM app_usage WHERE packageName = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (packageName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, packageName);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<String>() {
      @Override
      @Nullable
      public String call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final String _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getString(0);
            }
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalUsageTime(final String packageName, final long startTime,
      final long endTime, final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(usageTimeInMillis) FROM app_usage WHERE packageName = ? AND date >= ? AND date <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (packageName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, packageName);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyUsage>> getDailyUsage(final long startTime, final long endTime) {
    final String _sql = "SELECT date, SUM(usageTimeInMillis) as totalUsage FROM app_usage WHERE date >= ? AND date <= ? GROUP BY date";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"app_usage"}, new Callable<List<DailyUsage>>() {
      @Override
      @NonNull
      public List<DailyUsage> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = 0;
          final int _cursorIndexOfTotalUsage = 1;
          final List<DailyUsage> _result = new ArrayList<DailyUsage>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyUsage _item;
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final long _tmpTotalUsage;
            _tmpTotalUsage = _cursor.getLong(_cursorIndexOfTotalUsage);
            _item = new DailyUsage(_tmpDate,_tmpTotalUsage);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getControlledPackageNames(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT packageName FROM app_usage WHERE isControlled = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            if (_cursor.isNull(0)) {
              _item = null;
            } else {
              _item = _cursor.getString(0);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
