/**
 * new:
 *   2013.12.26 
 *     * class WrongPasswordException extends SessionCreationFailedException
 *     * class NotFoundUserException extends SessionCreationFailedException
 *     * hierarchy now is:
 *         LeOraException
 *           |-> SessionCreationFailedException
 *                  |-> NotFoundUserException 
 *                  |-> WrongPasswordException
 *     * changes in JUnit test (class TestLeOraDatabaseInstance)
 *        now they run without errors
 *     * current patch number incremented and instance.getVersion() = 1.1
 *     * added this package-info.java file
 *     * bug fix in LeOraBlock.getRowIndexForBulkInsert
 *     * LeOraSchema.createTable now return null or reference to LeOraTable instead of void as it was.
 *        Is it more easier to write code now because not need to call getTable() to get reference of 
 *        created table. 
 *     * changed method LeOraDatabaseInstance.createSession(): now will not create thread if value for parameter userRunnable is null.
 *        This is done for JUnit tests because JUnit doesn't correctly works with thread.
 *        In case of using JUnit take result value of LeOraDatabaseInstance.createSession() and use this LeOraSession object.
 *     * new methods getRowsCount(), getFreeRowsCount in the clases: LeOraTable, LeOraSegment, LeOraExtent, LeOraBlock
 *     * new methods:
 *         LeOraExtent.getFreeBlocksCount()
 *         LeOraExtent.getBlocksCount()
 *         LeOraSegment.getFreeExtentsCount()
 *         LeOraSegment.getExtentsCount()
 *         LeOraTable.getSegmentsCount()
 *         LeOraTable.getFreeSegmentsCount()
 *     * changes in LeOraDatabaseInstance.createSession() to be able to call it specifying parameter userRunnable = null.
 *       This is needed for JUnit test because JUnit doesn't correctly work with Runnable.
 *     * new JUnit's test3() in the file TestLeOraDatabaseInstance.java    
 *     * bug fixes in:
 *         LeOraBlock.getRowIndexForBulkInsert() 
 *         LeOraExtent.getBlockIndexForBulkInsert()
 *         LeOraSegment.getExtentIndexForBulkInsert()
 */
/**
 * @author (stelio=fapiro) Roman Kuzmin, fapiro@mail.ru, leorandi@mail.ru, gleorandi@gmail.com, www.leorandi.com
 */
package leorandiDatabase;