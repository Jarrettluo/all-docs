package com.jiaruiblog.service;

import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;
import com.jiaruiblog.entity.vo.TagVO;
import com.jiaruiblog.repository.TagRepository;
import com.jiaruiblog.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag testTag;
    private TagDocRelationship testRelationship;

    @BeforeEach
    void setUp() {
        testTag = new Tag();
        testTag.setId("test-id");
        testTag.setName("test-tag");
        testTag.setCreateDate(new Date());
        testTag.setUpdateDate(new Date());

        testRelationship = new TagDocRelationship();
        testRelationship.setId("rel-id");
        testRelationship.setTagId("test-id");
        testRelationship.setFileId("file-id");
        testRelationship.setCreateDate(new Date());
        testRelationship.setUpdateDate(new Date());
    }

    @Test
    void testInsertTag() {
        // Given
        when(tagRepository.findByName("test-tag")).thenReturn(Arrays.asList());
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        // When
        tagService.insert(testTag);

        // Then
        verify(tagRepository).save(testTag);
    }

    @Test
    void testQueryById() {
        // Given
        when(tagRepository.findById("test-id")).thenReturn(testTag);

        // When
        Tag result = tagService.queryByTagId("test-id");

        // Then
        assertNotNull(result);
        assertEquals("test-id", result.getId());
        assertEquals("test-tag", result.getName());
    }

    @Test
    void testAddRelationship() {
        // Given
        when(tagRepository.relationshipExists("test-id", "file-id")).thenReturn(false);
        when(tagRepository.saveRelationship(any())).thenReturn(testRelationship);

        // When
        tagService.addRelationShip(testRelationship);

        // Then
        verify(tagRepository).saveRelationship(testRelationship);
    }

    @Test
    void testQueryByDocId() {
        // Given
        List<TagDocRelationship> relationships = Arrays.asList(testRelationship);
        when(tagRepository.findRelationshipsByDocId("file-id")).thenReturn(relationships);
        when(tagRepository.findById("test-id")).thenReturn(testTag);

        // When
        List<TagVO> result = tagService.queryByDocId("file-id");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-id", result.get(0).getId());
        assertEquals("test-tag", result.get(0).getName());
    }

    @Test
    void testSaveOrUpdateBatch() {
        // Given
        List<String> tagNames = Arrays.asList("tag1", "tag2");
        when(tagRepository.findByNames(Arrays.asList("tag1", "tag2"))).thenReturn(Arrays.asList());
        when(tagRepository.saveAll(any())).thenReturn(Arrays.asList(testTag));

        // When
        List<String> result = tagService.saveOrUpdateBatch(tagNames);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tagRepository).saveAll(any());
    }

    @Test
    void testRelateExist() {
        // Given
        when(tagRepository.relationshipExists("test-id", "file-id")).thenReturn(true);

        // When
        boolean result = tagService.relateExist("test-id", "file-id");

        // Then
        assertTrue(result);
        verify(tagRepository).relationshipExists("test-id", "file-id");
    }

    @Test
    void testQueryDocIdListByTagId() {
        // Given
        List<TagDocRelationship> relationships = Arrays.asList(testRelationship);
        when(tagRepository.findRelationshipsByTagId("test-id")).thenReturn(relationships);

        // When
        List<String> result = tagService.queryDocIdListByTagId("test-id");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("file-id", result.get(0));
    }

    @Test
    void testFuzzySearchDoc() {
        // Given
        List<String> fileIds = Arrays.asList("file1", "file2");
        when(tagRepository.findFileIdsByTagNameRegex("java")).thenReturn(fileIds);

        // When
        List<String> result = tagService.fuzzySearchDoc("java");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("file1", result.get(0));
        assertEquals("file2", result.get(1));
    }

    @Test
    void testRemoveRelateByDocId() {
        // When
        tagService.removeRelateByDocId("doc-id");

        // Then
        verify(tagRepository).deleteRelationshipsByDocId("doc-id");
    }
} 